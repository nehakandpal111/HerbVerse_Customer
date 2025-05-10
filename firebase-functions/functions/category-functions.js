// Product Category management functions (vendor/admin only)
const functions = require('firebase-functions');
const admin = require('firebase-admin');

const firestore = admin.firestore();

// Get all product categories (vendor/admin only)
exports.getProductCategories = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Verify user is either a vendor or admin
    const userDoc = await firestore.collection('users').doc(userId).get();
    const vendorDoc = await firestore.collection('vendors').doc(userId).get();
    
    // Only vendors or admins can access categories management
    if (
      (!userDoc.exists || !userDoc.data().isAdmin) && 
      !vendorDoc.exists
    ) {
      throw new functions.https.HttpsError('permission-denied', 
        'Only vendors and admins can access category management');
    }
    
    const categoriesSnapshot = await firestore.collection('categories').get();
    const categories = categoriesSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    return { success: true, categories };
  } catch (error) {
    console.error('Error getting product categories:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch categories');
  }
});

// Add category (admin only)
exports.addCategory = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is an admin
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    // Verify user is an admin
    const userDoc = await firestore.collection('users').doc(context.auth.uid).get();
    if (!userDoc.exists || !userDoc.data().isAdmin) {
      throw new functions.https.HttpsError('permission-denied', 'Only admins can add categories');
    }
    
    // Validate category data
    if (!data.name) {
      throw new functions.https.HttpsError('invalid-argument', 'Category must have a name');
    }
    
    // Check if category with same name already exists
    const existingCategoriesSnapshot = await firestore
      .collection('categories')
      .where('name', '==', data.name)
      .get();
      
    if (!existingCategoriesSnapshot.empty) {
      throw new functions.https.HttpsError('already-exists', 'Category with this name already exists');
    }
    
    // Create new category
    const categoryData = {
      name: data.name,
      description: data.description || '',
      imageUrl: data.imageUrl || '',
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    const newCategoryRef = await firestore.collection('categories').add(categoryData);
    
    return { 
      success: true, 
      categoryId: newCategoryRef.id,
      message: 'Category added successfully' 
    };
  } catch (error) {
    console.error('Error adding category:', error);
    throw new functions.https.HttpsError('internal', 'Failed to add category');
  }
});

// Update category (admin only)
exports.updateCategory = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is an admin
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    // Verify user is an admin
    const userDoc = await firestore.collection('users').doc(context.auth.uid).get();
    if (!userDoc.exists || !userDoc.data().isAdmin) {
      throw new functions.https.HttpsError('permission-denied', 'Only admins can update categories');
    }
    
    if (!data.categoryId) {
      throw new functions.https.HttpsError('invalid-argument', 'Category ID is required');
    }
    
    // Check if category exists
    const categoryDoc = await firestore.collection('categories').doc(data.categoryId).get();
    if (!categoryDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Category not found');
    }
    
    // Prepare update data
    const updateData = {
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    if (data.name !== undefined) {
      // If name is being updated, check for duplicates
      if (data.name) {
        const existingCategoriesSnapshot = await firestore
          .collection('categories')
          .where('name', '==', data.name)
          .get();
          
        const hasDuplicate = existingCategoriesSnapshot.docs.some(doc => doc.id !== data.categoryId);
        if (hasDuplicate) {
          throw new functions.https.HttpsError('already-exists', 'Category with this name already exists');
        }
        
        updateData.name = data.name;
      } else {
        throw new functions.https.HttpsError('invalid-argument', 'Category name cannot be empty');
      }
    }
    
    if (data.description !== undefined) updateData.description = data.description;
    if (data.imageUrl !== undefined) updateData.imageUrl = data.imageUrl;
    
    // Update the category
    await firestore.collection('categories').doc(data.categoryId).update(updateData);
    
    return { 
      success: true, 
      message: 'Category updated successfully' 
    };
  } catch (error) {
    console.error('Error updating category:', error);
    throw new functions.https.HttpsError('internal', 'Failed to update category');
  }
});

// Delete category (admin only)
exports.deleteCategory = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated and is an admin
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    // Verify user is an admin
    const userDoc = await firestore.collection('users').doc(context.auth.uid).get();
    if (!userDoc.exists || !userDoc.data().isAdmin) {
      throw new functions.https.HttpsError('permission-denied', 'Only admins can delete categories');
    }
    
    if (!data.categoryId) {
      throw new functions.https.HttpsError('invalid-argument', 'Category ID is required');
    }
    
    // Check if category exists
    const categoryDoc = await firestore.collection('categories').doc(data.categoryId).get();
    if (!categoryDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Category not found');
    }
    
    // Check if there are products using this category
    const productsSnapshot = await firestore
      .collection('products')
      .where('categoryId', '==', data.categoryId)
      .limit(1)
      .get();
      
    if (!productsSnapshot.empty) {
      throw new functions.https.HttpsError(
        'failed-precondition', 
        'Cannot delete category that has products. Please reassign or delete the products first.'
      );
    }
    
    // Delete the category
    await firestore.collection('categories').doc(data.categoryId).delete();
    
    return { 
      success: true, 
      message: 'Category deleted successfully' 
    };
  } catch (error) {
    console.error('Error deleting category:', error);
    throw new functions.https.HttpsError('internal', 'Failed to delete category');
  }
});