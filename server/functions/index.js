
const admin = require('firebase-admin');
const firebase = require('firebase');
const functions = require('firebase-functions');
const serviceAccount = require("./firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");


/* APP SETTINGS INITIALIZING */
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

var authentication = require('./auth/auth.js');
var firebaseDB_Service = require('./utils/firebaseDB_Service.js');



/* AUTHENTICATION FUNCTIONS LISTENERS */
/* ================================== */
exports.addNewUser = functions.https.onRequest(function(request, response) {
	console.log('exports.addNewUser body ', request.body);

	authentication.addNewFirebaseUser(request.body, response);
});

exports.getUser = functions.https.onRequest(function(request, response) {
	console.log('exports.getUser body ', request.body);

	authentication.getUser(request.body.currentUserUid, response);
});

exports.logIn = functions.https.onRequest(function(request, response) {
	console.log('exports.logIn body ', request.body);
	
	authentication.logIn(request.body, response);
});

/* ================================== */




/* COMPETITIONS FUNCTIONS LISTENERS */
/* ================================ */
exports.getCompetitions = functions.https.onRequest(function(request, response) {
	console.log('exports.getCompetitions body ', request.body);
	
	firebaseDB_Service.getCompetitions(request.body.currentUser.uid, response);
});

exports.setNewCompetition = functions.https.onRequest(function(request, response) {
	console.log('exports.setNewCompetition body ', request.body);
	
	firebaseDB_Service.setNewCompetition(request.body, response);
});

exports.joinToCompetition = functions.https.onRequest(function(request, response) {
	console.log('exports.joinToCompetition body ', request.body);
	
	firebaseDB_Service.joinToCompetition(request.body, response);
});

exports.setCompetitionResults = functions.https.onRequest(function(request, response) {
	console.log('exports.setCompetitionResults body ', JSON.parse(request.body));
	
	firebaseDB_Service.setCompetitionResults(JSON.parse(request.body), response);
});
/* ================================ */
