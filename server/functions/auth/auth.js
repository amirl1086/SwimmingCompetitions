
const firebase = require('firebase');
const admin = require('firebase-admin');

const utilities = require('./../utils/utils.js');
const firebaseDB_Service = require('./../utils/firebaseDB_Service.js');

module.exports =  {

	logIn: function(idToken, response) {
		admin.auth().verifyIdToken(idToken).then(function(decodedToken) {
			console.log('decodedToken ', decodedToken);
		    firebaseDB_Service.getUser(decodedToken.uid, function(sucess, result) {
		    	if(sucess) {
		    		utilities.sendResponse(response, null, result);
		    	}
		    	else {
		    		utilities.sendResponse(response, result, null);
		    	}
			});
		})
		.catch(function(error) {
		  	utilities.sendResponse(response, error, null);
		});

/*		firebase.auth().signInWithEmailAndPassword(params.email, params.password).then(function(firebaseUser) {
			firebaseDB_Service.getUser(firebaseUser.uid, function(currentUser) {
				//will run after firebase finished retrieving new user
				console.log('logIn currentUser: ', currentUser);
				utilities.sendResponse(response, null, currentUser);
			});
		})
		.catch(function(error) {
			utilities.sendResponse(response, error, null);
		});*/
	},

	getUser: function(uid, response, callback) {
		firebaseDB_Service.getUser(uid, function(sucess, result) {
			if(sucess) {
				var currentUser = result;
				if(callback) {
					callback(currentUser);
				}
				else {
					utilities.sendResponse(response, null, currentUser);
				}
			}
			else {
				if(callback) {
					callback(null);
				}
				else {
					utilities.sendResponse(response, result, null);
				}
			}
			
		});
	},

	addNewFirebaseUser: function(params, response) {
		//create new user in with credantials
		console.log('params ', params);
		var newUser = {
			'email': params.email,
		  	'phoneNumber': '+972' + params.phoneNumber,
		  	'password': params.password,
			'displayName': params.firstName + '' + params.lastName
		}

		admin.auth().createUser(newUser).then(function(userRecord) {
			console.log('firebaseUser ', userRecord);
			//utilities.sendResponse(response, null, userRecord);
			firebaseDB_Service.addNewUser(userRecord, params, function(success, result) { 
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