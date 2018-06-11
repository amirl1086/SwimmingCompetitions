
const admin = require('firebase-admin');
const firebase = require('firebase');
const functions = require('firebase-functions');
const serviceAccount = require("./firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");
const authentication = require('./auth/auth.js');
const firebaseDB_Service = require('./utils/firebaseDB_Service.js');

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




/* AUTHENTICATION FUNCTIONS LISTENERS */
/* ================================== */
exports.addNewUser = functions.https.onRequest((request, response) => {
	authentication.addNewFirebaseUser(request.body, response);
});

exports.getUser = functions.https.onRequest((request, response) => {
	authentication.getUser(request.body.currentUserUid, response, null);
});

exports.logIn = functions.https.onRequest((request, response) => {
	authentication.logIn(request.body.idToken, response);
});

exports.updateUserDetails = functions.https.onRequest((request, response) => {
	authentication.updateUserDetails(request.body, response);
});

exports.updateFirebaseUser = functions.https.onRequest((request, response) => {
	authentication.updateFirebaseUser(request.body, response);
});




/* COMPETITIONS FUNCTIONS LISTENERS */
/* ================================ */
exports.getCompetitions = functions.https.onRequest((request, response) => {
	firebaseDB_Service.getCompetitions(request.body, response);
});

exports.getCompetitionInProgress = functions.https.onRequest((request, response) => {
	firebaseDB_Service.getCompetitionInProgress(response);
});

exports.setNewCompetition = functions.https.onRequest((request, response) => {
	firebaseDB_Service.setNewCompetition(request.body, response);
});

exports.getParticipantStatistics = functions.https.onRequest((request, response) => {
	firebaseDB_Service.getParticipantStatistics(request.body, response);
});

exports.joinToCompetition = functions.https.onRequest((request, response) => {
	firebaseDB_Service.joinToCompetition(request.body, response);
});

exports.setCompetitionResults = functions.https.onRequest((request, response) => {
	firebaseDB_Service.setCompetitionResults(request.body, response);
});

exports.addExistingUserToCompetition = functions.https.onRequest((request, response) => {
	firebaseDB_Service.addExistingUserToCompetition(request.body, response);
});

exports.initCompetitionForIterations = functions.https.onRequest((request, response) => {
	firebaseDB_Service.initCompetitionForIterations(request.body, response);
});

exports.getPersonalResults = functions.https.onRequest((request, response) => {
	firebaseDB_Service.getPersonalResultsByCompetitionId(request.body, response);
});

exports.cancelRegistration = functions.https.onRequest((request, response) => {
	firebaseDB_Service.cancelRegistration(request.body, response);
});




/* PARENTS FUNCTIONS LISTENERS */
/* ================================ */
exports.getUsersByParentId = functions.https.onRequest((request, response) => {
	authentication.getUsersByParentId(request.body, response);
});

exports.addChildToParent = functions.https.onRequest((request, response) => {
	authentication.addChildToParent(request.body, response);
});




/* MEDIA FUNCTIONS LISTENERS */
/* ================================ */
exports.addNewMedia = functions.https.onRequest((request, response) => {
	firebaseDB_Service.addNewMedia(request.body, response);
});

exports.getMediaByCompetitionId = functions.https.onRequest((request, response) => {
	firebaseDB_Service.getMediaByCompetitionId(request.body, response);
});
/* ================================ */




//db manipulation
/* exports.resetCompetitions = functions.https.onRequest((request, response) => {
	let db = admin.database();
	let collectionRef = db.ref('competitions');

	collectionRef.on('value', (snapshot) => {
		let competitions = snapshot.val();
		for (let compId in competitions) {
			for (let userId in competitions[compId].participants) {
				competitions[compId].participants[userId].competed = 'false';
			}
			competitions[compId].isDone = 'false';
			competitions[compId].inProgress = 'false';
			delete competitions[compId].currentParticipants;
		}
		collectionRef.update(competitions);

		collectionRef = db.ref('personalResults');
		collectionRef.remove();

		response.send('success\n');
	}, (error) => {
		response.send('error ', error, '\n');
	});
}); */