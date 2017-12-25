
var firebase = require('firebase-admin');
var functions = require('firebase-functions');

var serviceAccount = require("./firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://fir-swimmingcompetitions.firebaseio.com"
});


var auth = require('./auth/auth.js');

/*AUTHENTICATION FUNCTIONS LISTENERS*/
exports.addNewUser = functions.https.onRequest(function(request, response) {
	auth.addNewUser(request.params);
});
exports.logIn = functions.https.onRequest(function(request, response) {
	auth.logIn(request.params);
});

