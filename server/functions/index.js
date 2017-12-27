
const admin = require('firebase-admin');
const firebase = require('firebase');
const functions = require('firebase-functions');
const serviceAccount = require("./firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");

var config = {
	apiKey: "AIzaSyAJiubD80W9a1s8K9tx3yILgLZwPJZMziE",
    authDomain: "firebase-swimmingcompetitions.firebaseapp.com",
    databaseURL: "https://fir-swimmingcompetitions.firebaseio.com",
    projectId: "firebase-swimmingcompetitions",
    /*storageBucket: "firebase-swimmingcompetitions.appspot.com",*/
    messagingSenderId: "197819058733"

}

firebase.initializeApp(config);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://fir-swimmingcompetitions.firebaseio.com"
});


/*AUTHENTICATION FUNCTIONS LISTENERS*/
exports.addNewUser = functions.https.onRequest(function(request, response) {
	var reqBody = request.body;

	console.log('addNewUser body ', reqBody);

	var newUser = {
		firstName: reqBody.firstName,
		lastName: reqBody.lastName,
		email: reqBody.email,
		password: reqBody.password
	};

	firebase.auth().createUserWithEmailAndPassword(newUser.email, newUser.password).then(function(firebaseUser) {
		console.log('createUserWithEmailAndPassword user: ', firebaseUser);
		response.send(firebaseUser);
	}).catch(function(error) {
		console.log('createUserWithEmailAndPassword error: ', error);
		response.send(null);
	});
  	
});


exports.logIn = functions.https.onRequest(function(request, response) {
	var reqBody = request.body;

	console.log('logIn body ', request.body);

	var logInUser = {
		email: reqBody.email,
		password: reqBody.password
	};

	firebase.auth().signInWithEmailAndPassword(logInUser.email, logInUser.password).then(function(firebaseUser) {
		console.log('createUserWithEmailAndPassword user: ', firebaseUser);
		response.send(firebaseUser);
	}).catch(function(error) {
	  	console.log('signInWithEmailAndPassword error: ', error);
		response.send(null);
	});
});

