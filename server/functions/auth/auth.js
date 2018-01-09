
const firebase = require('firebase');

var utilities = require('./../utils/utils.js');
var firebaseDB_Service = require('./../utils/firebaseDB_Service.js');

module.exports =  {

	logIn: function(params, response) {
		firebase.auth().signInWithEmailAndPassword(params.email, params.password)
			.then(function(firebaseUser) {
				firebaseDB_Service.getUser(firebaseUser.uid, function(currentUser) {
					//will run after firebase finished retrieving new user
					console.log('logIn currentUser: ', currentUser);
					utilities.sendResponse(response, null, currentUser);
				});
			})
			.catch(function(error) {
				utilities.sendResponse(response, error, null);
			}
		);
	},

	addNewFirebaseUser: function(params, response) {
		//create new user in with credantials
		firebase.auth().createUserWithEmailAndPassword(params.email, params.password)
			//firebaseUser is the actual object from firebase
			.then(function(firebaseUser) {
				firebaseDB_Service.addNewUser(firebaseUser, params, function(currentUser) { 
					//will run after firebase finished inserting new user
					console.log('addNewFirebaseUser currentUser: ', currentUser);
					utilities.sendResponse(response, null, currentUser); 
				});
			})
			.catch(function(error) {
				utilities.sendResponse(response, error, null);
			}
		);
	},

};