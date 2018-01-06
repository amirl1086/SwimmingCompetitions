
const firebase = require('firebase');

var utilities = require('./../utils/utils.js');
var firebaseDB_Service = require('./../utils/firebaseDB_Service.js');

module.exports =  {

	logIn: function(params, response) {
		console.log('logIn params ', params);

		firebase.auth().signInWithEmailAndPassword(params.email, params.password).then(function(firebaseUser) {
			utilities.sendResponse(response, null, firebaseUser);
		}).catch(function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	addNewFirebaseUser: function(params, response) {
		console.log('addNewFirebaseUser params ', params);

		firebase.auth().createUserWithEmailAndPassword(params.email, params.password).then(function(firebaseUser) {
			console.log('addNewFirebaseUser createUserWithEmailAndPassword success ', firebaseUser);

			firebaseDB_Service.addNewUser(firebaseUser, params, response);
		}).catch(function(error) {
			console.log('addNewFirebaseUser createUserWithEmailAndPassword error ', error);

			utilities.sendResponse(response, error, null);
		});
	},

};