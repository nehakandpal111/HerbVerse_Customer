// Order processing functions
const functions = require('firebase-functions');
const admin = require('firebase-admin');

const firestore = admin.firestore();

// Create a new order (customer only)
exports.createOrder = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Validate order data
    if (!data.items || !Array.isArray(data.items) || data.items.length === 0) {
      throw new functions.https.HttpsError('invalid-argument', 'Order must contain items');
    }
    
    // Get cart items and calculate total
    const orderItems = [];
    let orderTotal = 0;
    
    // Process each order item
    for (const item of data.items) {
      if (!item.productId || !item.quantity) {
        throw new functions.https.HttpsError('invalid-argument', 'Each item must have a productId and quantity');
      }
      
      // Get product data to ensure it exists and get current price
      const productDoc = await firestore.collection('products').doc(item.productId).get();
      
      if (!productDoc.exists) {
        throw new functions.https.HttpsError('not-found', `Product ${item.productId} not found`);
      }
      
      const productData = productDoc.data();
      
      // Check if there's enough stock
      if (productData.stock < item.quantity) {
        throw new functions.https.HttpsError(
          'failed-precondition', 
          `Not enough stock for ${productData.name}. Available: ${productData.stock}`
        );
      }
      
      const itemTotal = productData.price * item.quantity;
      
      // Add to order items
      orderItems.push({
        productId: item.productId,
        productName: productData.name,
        quantity: item.quantity,
        price: productData.price,
        total: itemTotal,
        vendorId: productData.vendorId
      });
      
      // Add to order total
      orderTotal += itemTotal;
    }
    
    // Get shipping address
    const shippingAddress = data.shippingAddress || {};
    
    if (!shippingAddress.address || !shippingAddress.city || !shippingAddress.postalCode) {
      throw new functions.https.HttpsError('invalid-argument', 'Valid shipping address is required');
    }
    
    // Create new order
    const orderData = {
      userId,
      items: orderItems,
      total: orderTotal,
      shippingAddress: shippingAddress,
      status: 'pending',
      paymentStatus: 'pending',
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    // Save order to Firestore
    const newOrderRef = await firestore.collection('orders').add(orderData);
    
    // Update product stock
    for (const item of data.items) {
      const productRef = firestore.collection('products').doc(item.productId);
      await productRef.update({
        stock: admin.firestore.FieldValue.increment(-item.quantity),
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
    }
    
    // Create separate vendor orders
    const vendorOrders = {};
    
    // Group items by vendor
    for (const item of orderItems) {
      if (!vendorOrders[item.vendorId]) {
        vendorOrders[item.vendorId] = {
          items: [],
          total: 0
        };
      }
      
      vendorOrders[item.vendorId].items.push(item);
      vendorOrders[item.vendorId].total += item.total;
    }
    
    // Create vendor-specific orders
    for (const vendorId in vendorOrders) {
      await firestore.collection('vendor-orders').add({
        mainOrderId: newOrderRef.id,
        vendorId,
        userId,
        items: vendorOrders[vendorId].items,
        total: vendorOrders[vendorId].total,
        shippingAddress: shippingAddress,
        status: 'pending',
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
    }
    
    // Clear customer's cart after successful order
    const cartRef = firestore.collection('carts').doc(userId);
    const cartItemsSnapshot = await cartRef.collection('items').get();
    
    const batch = firestore.batch();
    cartItemsSnapshot.docs.forEach(doc => {
      batch.delete(doc.ref);
    });
    await batch.commit();
    
    return { 
      success: true, 
      orderId: newOrderRef.id,
      message: 'Order created successfully' 
    };
  } catch (error) {
    console.error('Error creating order:', error);
    throw new functions.https.HttpsError('internal', `Failed to create order: ${error.message}`);
  }
});

// Get customer orders (customer only)
exports.getCustomerOrders = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Get all orders for this customer
    const ordersSnapshot = await firestore.collection('orders')
      .where('userId', '==', userId)
      .orderBy('createdAt', 'desc')
      .get();
      
    const orders = ordersSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
      createdAt: doc.data().createdAt ? doc.data().createdAt.toDate().toISOString() : null
    }));
    
    return { success: true, orders };
  } catch (error) {
    console.error('Error getting customer orders:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch orders');
  }
});

// Get vendor orders (vendor only)
exports.getVendorOrders = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const vendorId = context.auth.uid;
    
    // Verify user is a vendor
    const vendorDoc = await firestore.collection('vendors').doc(vendorId).get();
    
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('permission-denied', 'Only vendors can access vendor orders');
    }
    
    // Get all orders for this vendor
    const vendorOrdersSnapshot = await firestore.collection('vendor-orders')
      .where('vendorId', '==', vendorId)
      .orderBy('createdAt', 'desc')
      .get();
      
    const orders = vendorOrdersSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
      createdAt: doc.data().createdAt ? doc.data().createdAt.toDate().toISOString() : null
    }));
    
    return { success: true, orders };
  } catch (error) {
    console.error('Error getting vendor orders:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch vendor orders');
  }
});

// Update order status (vendor only)
exports.updateOrderStatus = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    if (!data.orderId || !data.status) {
      throw new functions.https.HttpsError('invalid-argument', 'Order ID and status are required');
    }
    
    const vendorId = context.auth.uid;
    
    // Verify user is a vendor
    const vendorDoc = await firestore.collection('vendors').doc(vendorId).get();
    
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('permission-denied', 'Only vendors can update order status');
    }
    
    // Get the vendor order to check ownership
    const orderDoc = await firestore.collection('vendor-orders').doc(data.orderId).get();
    
    if (!orderDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Order not found');
    }
    
    const orderData = orderDoc.data();
    
    // Verify the vendor owns this order
    if (orderData.vendorId !== vendorId) {
      throw new functions.https.HttpsError('permission-denied', 'You can only update your own orders');
    }
    
    // Validate status
    const validStatuses = ['pending', 'processing', 'shipped', 'delivered', 'cancelled'];
    if (!validStatuses.includes(data.status)) {
      throw new functions.https.HttpsError('invalid-argument', 'Invalid order status');
    }
    
    // Update the order status
    await firestore.collection('vendor-orders').doc(data.orderId).update({
      status: data.status,
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    // If this is a cancellation, restore the stock
    if (data.status === 'cancelled' && orderData.status !== 'cancelled') {
      for (const item of orderData.items) {
        const productRef = firestore.collection('products').doc(item.productId);
        await productRef.update({
          stock: admin.firestore.FieldValue.increment(item.quantity),
          updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
      }
    }
    
    // Check all vendor orders statuses to update the main order status
    const mainOrderId = orderData.mainOrderId;
    const allVendorOrdersSnapshot = await firestore.collection('vendor-orders')
      .where('mainOrderId', '==', mainOrderId)
      .get();
    
    const allStatuses = allVendorOrdersSnapshot.docs.map(doc => doc.data().status);
    
    // Determine the "effective" status for the main order
    let mainOrderStatus;
    
    if (allStatuses.every(status => status === 'delivered')) {
      mainOrderStatus = 'delivered';
    } else if (allStatuses.every(status => status === 'cancelled')) {
      mainOrderStatus = 'cancelled';
    } else if (allStatuses.some(status => status === 'shipped')) {
      mainOrderStatus = 'shipped';
    } else if (allStatuses.some(status => status === 'processing')) {
      mainOrderStatus = 'processing';
    } else {
      mainOrderStatus = 'pending';
    }
    
    // Update the main order status
    await firestore.collection('orders').doc(mainOrderId).update({
      status: mainOrderStatus,
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    return { 
      success: true, 
      message: 'Order status updated successfully' 
    };
  } catch (error) {
    console.error('Error updating order status:', error);
    throw new functions.https.HttpsError('internal', 'Failed to update order status');
  }
});