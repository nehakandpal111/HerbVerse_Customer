// User management functions
const functions = require('firebase-functions');
const admin = require('firebase-admin');

const firestore = admin.firestore();

// Get user profile
exports.getUserProfile = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Get user data
    const userDoc = await firestore.collection('users').doc(userId).get();
    
    if (!userDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'User profile not found');
    }
    
    const userData = userDoc.data();
    
    // Don't return sensitive data
    delete userData.password;
    delete userData.authTokens;
    
    return {
      success: true,
      profile: {
        id: userDoc.id,
        ...userData
      }
    };
  } catch (error) {
    console.error('Error getting user profile:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch user profile');
  }
});

// Update user profile
exports.updateUserProfile = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Check if user exists
    const userDoc = await firestore.collection('users').doc(userId).get();
    if (!userDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'User not found');
    }
    
    // Prepare update data
    const updateData = {
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    // Only update allowed fields
    if (data.name !== undefined) updateData.name = data.name;
    if (data.phone !== undefined) updateData.phone = data.phone;
    if (data.address !== undefined) updateData.address = data.address;
    if (data.profileImage !== undefined) updateData.profileImage = data.profileImage;
    if (data.notificationPreferences !== undefined) {
      updateData.notificationPreferences = data.notificationPreferences;
    }
    
    // Update the user
    await firestore.collection('users').doc(userId).update(updateData);
    
    return { 
      success: true, 
      message: 'Profile updated successfully' 
    };
  } catch (error) {
    console.error('Error updating user profile:', error);
    throw new functions.https.HttpsError('internal', 'Failed to update profile');
  }
});

// Create customer account after Firebase Auth sign up
exports.createCustomerAccount = functions.auth.user().onCreate(async (user) => {
  try {
    // Skip if user's email is not verified (if verification required)
    // if (!user.emailVerified) return;
    
    // Check if customer document already exists
    const userDoc = await firestore.collection('users').doc(user.uid).get();
    
    if (!userDoc.exists) {
      // Create new customer in Firestore
      await firestore.collection('users').doc(user.uid).set({
        email: user.email,
        name: user.displayName || '',
        phone: user.phoneNumber || '',
        profileImage: user.photoURL || '',
        address: '',
        type: 'customer',
        rewardPoints: 0,
        membershipTier: 'Bronze',
        notificationPreferences: {
          pushNotifications: true,
          emailNotifications: true,
          orderUpdates: true,
          promotions: false
        },
        isEmailVerified: user.emailVerified,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
      
      console.log(`Customer account created for ${user.email}`);
    }
  } catch (error) {
    console.error('Error creating customer account:', error);
  }
});

// Request to become a vendor
exports.requestVendorAccount = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Check if user exists
    const userDoc = await firestore.collection('users').doc(userId).get();
    if (!userDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'User not found');
    }
    
    // Check if already a vendor
    const vendorDoc = await firestore.collection('vendors').doc(userId).get();
    if (vendorDoc.exists) {
      throw new functions.https.HttpsError('already-exists', 'User is already a vendor');
    }
    
    // Check if there's a pending request
    const pendingRequestsSnapshot = await firestore
      .collection('vendor-requests')
      .where('userId', '==', userId)
      .where('status', '==', 'pending')
      .get();
      
    if (!pendingRequestsSnapshot.empty) {
      throw new functions.https.HttpsError('already-exists', 'You already have a pending vendor request');
    }
    
    // Validate required vendor information
    if (!data.businessName || !data.businessAddress || !data.contactPhone) {
      throw new functions.https.HttpsError(
        'invalid-argument', 
        'Business name, address, and contact phone are required'
      );
    }
    
    // Create vendor request
    const requestData = {
      userId,
      userEmail: context.auth.token.email || '',
      userName: userDoc.data().name || '',
      businessName: data.businessName,
      businessAddress: data.businessAddress,
      contactPhone: data.contactPhone,
      businessDescription: data.businessDescription || '',
      businessWebsite: data.businessWebsite || '',
      businessDocuments: data.businessDocuments || [],
      status: 'pending',
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    await firestore.collection('vendor-requests').add(requestData);
    
    return { 
      success: true, 
      message: 'Vendor request submitted successfully' 
    };
  } catch (error) {
    console.error('Error requesting vendor account:', error);
    throw new functions.https.HttpsError('internal', 'Failed to submit vendor request');
  }
});

// Get vendor profile (for vendors only)
exports.getVendorProfile = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Get vendor data
    const vendorDoc = await firestore.collection('vendors').doc(userId).get();
    
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Vendor profile not found');
    }
    
    return {
      success: true,
      profile: {
        id: vendorDoc.id,
        ...vendorDoc.data()
      }
    };
  } catch (error) {
    console.error('Error getting vendor profile:', error);
    throw new functions.https.HttpsError('internal', 'Failed to fetch vendor profile');
  }
});

// Update vendor profile (for vendors only)
exports.updateVendorProfile = functions.https.onCall(async (data, context) => {
  try {
    // Check if the user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }
    
    const userId = context.auth.uid;
    
    // Check if vendor exists
    const vendorDoc = await firestore.collection('vendors').doc(userId).get();
    if (!vendorDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'Vendor not found');
    }
    
    // Prepare update data
    const updateData = {
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    };
    
    // Only update allowed fields
    if (data.businessName !== undefined) updateData.businessName = data.businessName;
    if (data.businessAddress !== undefined) updateData.businessAddress = data.businessAddress;
    if (data.contactPhone !== undefined) updateData.contactPhone = data.contactPhone;
    if (data.businessDescription !== undefined) updateData.businessDescription = data.businessDescription;
    if (data.businessWebsite !== undefined) updateData.businessWebsite = data.businessWebsite;
    if (data.businessLogo !== undefined) updateData.businessLogo = data.businessLogo;
    if (data.businessBanner !== undefined) updateData.businessBanner = data.businessBanner;
    
    // Update the vendor
    await firestore.collection('vendors').doc(userId).update(updateData);
    
    return { 
      success: true, 
      message: 'Vendor profile updated successfully' 
    };
  } catch (error) {
    console.error('Error updating vendor profile:', error);
    throw new functions.https.HttpsError('internal', 'Failed to update vendor profile');
  }
});