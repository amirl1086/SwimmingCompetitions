
const firebase = require('firebase');

var utilities = require('./../utils/utils.js');
var firebaseDB_Service = require('./../utils/firebaseDB_Service.js');

module.exports =  {

	logIn: function(params, response) {
		firebase.auth().signInWithEmailAndPassword(params.email, params.password).then(function(firebaseUser) {
			firebaseDB_Service.getUser(firebaseUser.uid, function(currentUser) {
				//will run after firebase finished retrieving new user
				console.log('logIn currentUser: ', currentUser);
				utilities.sendResponse(response, null, currentUser);
			});
		})
		.catch(function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	getUser: function(uid, response, callback) {
		firebaseDB_Service.getUser(uid, function(currentUser) {
			if(currentUser && currentUser.uid) {
				if(callback) {
					callback(currentUser);
				}
				else {
					utilities.sendResponse(response, null, currentUser);
				}
			}
			else {
				if(callback) {
					return null;
				}
				else {
					utilities.sendResponse(response, currentUser, null);
				}
			}
		});
	},

	addNewFirebaseUser: function(params, response) {
		//create new user in with credantials
		firebase.auth().createUserWithEmailAndPassword(params.email, params.password).then(function(firebaseUser) {
			console.log('firebaseUser ', firebaseUser);
			firebaseDB_Service.addNewUser(firebaseUser, params, function(success, result) { 
				if(success) {
					utilities.sendResponse(response, null, result); 
				}
				else {
					utilities.sendResponse(response, result, null);
				}
				
			});
		})
		.catch(function(error) {
			console.log('addNewFirebaseUser error: ', error);
			utilities.sendResponse(response, error, null);
		});
	}

};