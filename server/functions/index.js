
const admin = require('firebase-admin');
const firebase = require('firebase');
const functions = require('firebase-functions');
const serviceAccount = require("./firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");


/* APP SETTINGS INITIALIZING */
const config = {
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
  databaseURL: config.databaseURL
});

const authentication = require('./auth/auth.js');
const firebaseDB_Service = require('./utils/firebaseDB_Service.js');



/* AUTHENTICATION FUNCTIONS LISTENERS */
/* ================================== */
exports.addNewUser = functions.https.onRequest(function(request, response) {
	authentication.addNewFirebaseUser(request.body, response);
});

exports.getUser = functions.https.onRequest(function(request, response) {
	authentication.getUser(request.body.currentUserUid, response);
});

exports.logIn = functions.https.onRequest(function(request, response) {
	authentication.logIn(request.body.idToken, response);
});

exports.updateFirebaseUser = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.updateFirebaseUser(request.body, response);
});

/* ================================== */




/* COMPETITIONS FUNCTIONS LISTENERS */
/* ================================ */
exports.getCompetitions = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.getCompetitions(request.body, response);
});

exports.setNewCompetition = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.setNewCompetition(request.body, response);
});

exports.joinToCompetition = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.joinToCompetition(request.body, response);
});

exports.setCompetitionResults = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.setCompetitionResults(request.body, response);
});

exports.addExistingUserToCompetition = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.addExistingUserToCompetition(request.body, response);
});

exports.initCompetitionForIterations = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.initCompetitionForIterations(request.body, response);
});

exports.getUsersByParentId = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.getUsersByFilters(request.body, response);
});

exports.addChildToParent = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.addChildToParent(request.body, response);
});

exports.getPersonalResults = functions.https.onRequest(function(request, response) {
	var params = request.body;

	if(params.competition) {
		firebaseDB_Service.getPersonalResultsByCompetitionId(params, response);
	}
	else {
		firebaseDB_Service.getPersonalResults(params, response);
	}
});

exports.cancelRegistration = functions.https.onRequest(function(request, response) {
	firebaseDB_Service.cancelRegistration(request.body, response);
});
/* ================================ */
