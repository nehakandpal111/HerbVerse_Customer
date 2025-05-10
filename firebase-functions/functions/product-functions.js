// Product management functions
const functions = require('firebase-functions');
const admin = require('firebase-admin');

const firestore = admin.firestore();

// Get all products (available to both vendors and customers)
exports.getAllProducts = functions.https.onCall(async (data, context) => {
  try {
    const productsSnapshot = await firestore.collection('products').get();
    const products = productsSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    return { success: true, products };
  } catch (error) {
    console.error('Error getting products:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch products');
  }
});

// Get products by category
exports.getProductsByCategory = functions.https.onCall(async (data, context) => {
  try {
    if (!data.categoryId) {
      throw new functions.https.HttpsError('invalid-argument', 'Category ID is required');
    }
    
    const productsSnapshot = await firestore
      .collection('products')
      .where('categoryId', '==', data.categoryId)
      .get();
      
    const products = productsSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    return { success: true, products };
  } catch (error) {
    console.error('Error getting products by category:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch products by category');
  }
});

// Add a new product (vendor only)
exports.addProduct = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const vendorSnapshot = await firestore
      .collection('vendors')
      .doc(context.auth.uid)
      .get();
      
    if (!vendorSnapshot.exists) {
      throw new functions.https.HttpsError('permission-denied', 'Only vendors can add products');
    }
    
    // Validate product data
    if (!data.name || !data.price || !data.categoryId) {
      throw new functions.https.HttpsError(
        'invalid-argument', 
        'Product must have a name, price, and category'
      );
    }
    
    // Create new product with vendor ID
    const productData = {
      name: data.name,
      shortDescription: data.shortDescription || '',
      fullDescription: data.fullDescription || '',
      price: parseFloat(data.price),
      stock: parseInt(data.stock || 0),
      categoryId: data.categoryId,
      imageUrl: data.imageUrl || '',
      vendorId: context.auth.uid,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    const newProductRef = await firestore.collection('products').add(productData);
    
    return { 
      success: true, 
      productId: newProductRef.id,
      message: 'Product added successfully' 
    };
  } catch (error) {
    console.error('Error adding product:', error);
    throw new functions.https.HttpsError('internal', 'Failed to add product');
  }
});

// Update a product (vendor only)
exports.updateProduct = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    if (!data.productId) {
      throw new functions.https.HttpsError('invalid-argument', 'Product ID is required');
    }
    
    // Get the product to check ownership
    const productDoc = await firestore.collection('products').doc(data.productId).get();
    
    if (!productDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Product not found');
    }
    
    const productData = productDoc.data();
    
    // Verify the vendor owns this product
    if (productData.vendorId !== context.auth.uid) {
      throw new functions.https.HttpsError('permission-denied', 'You can only update your own products');
    }
    
    // Update the product
    const updateData = {
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    if (data.name) updateData.name = data.name;
    if (data.shortDescription !== undefined) updateData.shortDescription = data.shortDescription;
    if (data.fullDescription !== undefined) updateData.fullDescription = data.fullDescription;
    if (data.price !== undefined) updateData.price = parseFloat(data.price);
    if (data.stock !== undefined) updateData.stock = parseInt(data.stock);
    if (data.categoryId) updateData.categoryId = data.categoryId;
    if (data.imageUrl !== undefined) updateData.imageUrl = data.imageUrl;
    
    await firestore.collection('products').doc(data.productId).update(updateData);
    
    return { 
      success: true, 
      message: 'Product updated successfully' 
    };
  } catch (error) {
    console.error('Error updating product:', error);
    throw new functions.https.HttpsError('internal', 'Failed to update product');
  }
});

// Delete a product (vendor only)
exports.deleteProduct = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is a vendor
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    if (!data.productId) {
      throw new functions.https.HttpsError('invalid-argument', 'Product ID is required');
    }
    
    // Get the product to check ownership
    const productDoc = await firestore.collection('products').doc(data.productId).get();
    
    if (!productDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Product not found');
    }
    
    const productData = productDoc.data();
    
    // Verify the vendor owns this product
    if (productData.vendorId !== context.auth.uid) {
      throw new functions.https.HttpsError('permission-denied', 'You can only delete your own products');
    }
    
    // Delete the product
    await firestore.collection('products').doc(data.productId).delete();
    
    return { 
      success: true, 
      message: 'Product deleted successfully' 
    };
  } catch (error) {
    console.error('Error deleting product:', error);
    throw new functions.https.HttpsError('internal', 'Failed to delete product');
  }
});