
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
//admin.initializeApp(functions.config().firebase);


var serviceAccount = require("../firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://fir-swimmingcompetitions.firebaseio.com"
});

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addNewUser = functions.https.onRequest(function(request, result) {
  // Grab the text parameter.
  console.log('REQ : \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n', request, '\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n');
  console.log('REQ QUERY: \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n', request.query, '\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n');

  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  admin.database().ref('/user').push('test').then(function(fbResult) {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    console.log('FB RESULT : \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n', fbResult, '\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n');
  });
});
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
