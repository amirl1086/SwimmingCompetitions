
const firebase = require('firebase');

var utilities = require('./../utils/utils.js');

module.exports =  {

	logIn: function(params, response) {
		
		var userCredentials = {
			email: params.email,
			password: params.password
		};

		firebase.auth().signInWithEmailAndPassword(userCredentials.email, userCredentials.password).then(function(firebaseUser) {
			utilities.sendResponse(response, null, firebaseUser);
		}).catch(function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	addNewUser: function(params, response) {
		var newUser = {
			firstName: reqBody.firstName,
			lastName: reqBody.lastName,
			email: reqBody.email,
			password: reqBody.password
		};

		firebase.auth().createUserWithEmailAndPassword(newUser.email, newUser.password).then(function(firebaseUser) {
			utilities.sendResponse(response, null, firebaseUser);
		}).catch(function(error) {
			utilities.sendResponse(response, error, null);
		});
	}

};