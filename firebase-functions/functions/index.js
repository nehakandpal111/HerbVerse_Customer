// Main entry point for Firebase Functions
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const productFunctions = require('./product-functions');
const orderFunctions = require('./order-functions');
// Product categories for vendors and admins only
const categoryFunctions = require('./category-functions');
const userFunctions = require('./user-functions');
const vendorFunctions = require('./vendor-functions');

// Initialize Firebase Admin
admin.initializeApp();

// Export all the functions
exports.products = productFunctions;
exports.orders = orderFunctions;
exports.categories = categoryFunctions;
exports.users = userFunctions;
exports.vendors = vendorFunctions;