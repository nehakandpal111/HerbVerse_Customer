// Vendor management functions
const functions = require('firebase-functions');
const admin = require('firebase-admin');

const firestore = admin.firestore();

// Get vendor dashboard data
exports.getVendorDashboard = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const vendorId = context.auth.uid;
    
    // Verify user is a vendor
    const vendorDoc = await firestore.collection('vendors').doc(vendorId).get();
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('permission-denied', 'Only vendors can access the vendor dashboard');
    }
    
    // Get recent orders
    const recentOrdersSnapshot = await firestore.collection('vendor-orders')
      .where('vendorId', '==', vendorId)
      .orderBy('createdAt', 'desc')
      .limit(5)
      .get();
      
    const recentOrders = recentOrdersSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
      createdAt: doc.data().createdAt ? doc.data().createdAt.toDate().toISOString() : null
    }));
    
    // Get product count
    const productsSnapshot = await firestore.collection('products')
      .where('vendorId', '==', vendorId)
      .get();
      
    const productCount = productsSnapshot.size;
    
    // Get low stock products
    const lowStockProductsSnapshot = await firestore.collection('products')
      .where('vendorId', '==', vendorId)
      .where('stock', '<', 10)
      .limit(5)
      .get();
      
    const lowStockProducts = lowStockProductsSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    // Get total sales
    let totalSales = 0;
    const allOrdersSnapshot = await firestore.collection('vendor-orders')
      .where('vendorId', '==', vendorId)
      .get();
      
    allOrdersSnapshot.forEach(doc => {
      const orderData = doc.data();
      if (orderData.status !== 'cancelled') {
        totalSales += orderData.total || 0;
      }
    });
    
    // Get monthly revenue data for chart
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);
    
    const monthlyOrdersSnapshot = await firestore.collection('vendor-orders')
      .where('vendorId', '==', vendorId)
      .where('createdAt', '>=', sixMonthsAgo)
      .orderBy('createdAt', 'asc')
      .get();
      
    // Process monthly revenue
    const monthlyRevenue = {};
    
    monthlyOrdersSnapshot.forEach(doc => {
      const orderData = doc.data();
      if (orderData.status !== 'cancelled' && orderData.createdAt) {
        const orderDate = orderData.createdAt.toDate();
        const monthYear = `${orderDate.getMonth() + 1}/${orderDate.getFullYear()}`;
        
        if (!monthlyRevenue[monthYear]) {
          monthlyRevenue[monthYear] = 0;
        }
        
        monthlyRevenue[monthYear] += orderData.total || 0;
      }
    });
    
    // Convert to array for charting
    const revenueData = Object.keys(monthlyRevenue).map(month => ({
      month,
      revenue: monthlyRevenue[month]
    }));
    
    return {
      success: true,
      dashboard: {
        recentOrders,
        productCount,
        lowStockProducts,
        totalSales,
        revenueData
      }
    };
  } catch (error) {
    console.error('Error getting vendor dashboard:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch vendor dashboard data');
  }
});

// Get vendor products
exports.getVendorProducts = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const vendorId = context.auth.uid;
    
    // Verify user is a vendor
    const vendorDoc = await firestore.collection('vendors').doc(vendorId).get();
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('permission-denied', 'Only vendors can access vendor products');
    }
    
    // Get products with pagination
    const limit = data.limit || 10;
    const startAfter = data.startAfter || null;
    
    let query = firestore.collection('products')
      .where('vendorId', '==', vendorId)
      .orderBy('createdAt', 'desc')
      .limit(limit);
      
    if (startAfter) {
      const startAfterDoc = await firestore.collection('products').doc(startAfter).get();
      if (startAfterDoc.exists) {
        query = query.startAfter(startAfterDoc);
      }
    }
    
    const productsSnapshot = await query.get();
    
    const products = productsSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    // Get total count for pagination
    const countSnapshot = await firestore.collection('products')
      .where('vendorId', '==', vendorId)
      .get();
      
    const totalCount = countSnapshot.size;
    
    return {
      success: true,
      products,
      totalCount,
      hasMore: products.length === limit
    };
  } catch (error) {
    console.error('Error getting vendor products:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch vendor products');
  }
});

// Get vendor sales report
exports.getVendorSalesReport = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const vendorId = context.auth.uid;
    
    // Verify user is a vendor
    const vendorDoc = await firestore.collection('vendors').doc(vendorId).get();
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('permission-denied', 'Only vendors can access sales reports');
    }
    
    // Parse date range
    let startDate = new Date();
    let endDate = new Date();
    
    if (data.period === 'week') {
      startDate.setDate(startDate.getDate() - 7);
    } else if (data.period === 'month') {
      startDate.setMonth(startDate.getMonth() - 1);
    } else if (data.period === 'year') {
      startDate.setFullYear(startDate.getFullYear() - 1);
    } else if (data.startDate && data.endDate) {
      startDate = new Date(data.startDate);
      endDate = new Date(data.endDate);
    }
    
    // Get orders in date range
    const ordersSnapshot = await firestore.collection('vendor-orders')
      .where('vendorId', '==', vendorId)
      .where('createdAt', '>=', startDate)
      .where('createdAt', '<=', endDate)
      .orderBy('createdAt', 'desc')
      .get();
      
    const orders = ordersSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data(),
      createdAt: doc.data().createdAt ? doc.data().createdAt.toDate().toISOString() : null
    }));
    
    // Calculate totals and stats
    let totalSales = 0;
    let totalOrders = orders.length;
    let totalItems = 0;
    const productSales = {};
    
    orders.forEach(order => {
      if (order.status !== 'cancelled') {
        totalSales += order.total || 0;
        
        // Count items and track product performance
        (order.items || []).forEach(item => {
          totalItems += item.quantity || 0;
          
          if (!productSales[item.productId]) {
            productSales[item.productId] = {
              productId: item.productId,
              productName: item.productName,
              totalQuantity: 0,
              totalRevenue: 0
            };
          }
          
          productSales[item.productId].totalQuantity += item.quantity || 0;
          productSales[item.productId].totalRevenue += item.total || 0;
        });
      }
    });
    
    // Convert to array and sort by revenue
    const topProducts = Object.values(productSales)
      .sort((a, b) => b.totalRevenue - a.totalRevenue)
      .slice(0, 5);
    
    return {
      success: true,
      report: {
        period: {
          start: startDate.toISOString(),
          end: endDate.toISOString()
        },
        summary: {
          totalSales,
          totalOrders,
          totalItems,
          averageOrderValue: totalOrders > 0 ? totalSales / totalOrders : 0
        },
        topProducts,
        orders
      }
    };
  } catch (error) {
    console.error('Error generating vendor sales report:', error);
    throw new functions.https.HttpsError('internal', 'Failed to generate sales report');
  }
});