
const firebase = require('firebase');
const admin = require('firebase-admin');

const utilities = require('./../utils/utils.js');
const firebaseDB_Service = require('./../utils/firebaseDB_Service.js');

module.exports =  {

	logIn: function(idToken, response) {
		admin.auth().verifyIdToken(idToken).then(function(decodedToken) {
			var currentUid = decodedToken.uid;
		    getUser(currentUid, null, function(sucess, result) {
		    	if(sucess) {
		    		if(!result) {
		    			var userParams = { 
		    				'firstName': decodedToken.name.substr(0, decodedToken.name.indexOf(' ')),
		    				'lastName': decodedToken.name.substr(decodedToken.name.indexOf(' ') + 1),
		    				'email': decodedToken.email,
		    				'uid': currentUid
		    			};
		    			firebaseDB_Service.addNewUser({ 'uid': currentUid }, userParams, function(success, result) { 
							if(success) {
								utilities.sendResponse(response, null, result); 
							}
							else {
								utilities.sendResponse(response, result, null);
							}
						});
		    		}
		    		else {
		    			utilities.sendResponse(response, null, result);
		    		}
		    	}
		    	else {
		    		utilities.sendResponse(response, result, null);
		    	}
			});
		})
		.catch(function(error) {
		  	utilities.sendResponse(response, error, null);
		});
	},

	getUser: function(uid, response, callback) {
		getUser(uid, response, callback);
	},

	addNewFirebaseUser: function(params, response) {
		//create new user in with credantials
		console.log('params ', params);
		var newUser = {
			'email': params.email,
		  	'password': params.password,
			'displayName': params.firstName + ' ' + params.lastName
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
	},

	getFirebaseUser: function(uid, callback) {
		admin.auth().getUser(uid).then(function(result) {
			callback(true, result);
		})
		.catch(function(error) {
			callback(false, error);
		});
	}

};

let getUser = (uid, response, callback) => {
	let db = admin.database();
	let userRef = db.ref('users/' + uid);

	userRef.on('value', function(snapshot) {
		if(callback) {
			callback(true, snapshot.val());
		}
		else {
			utilities.sendResponse(response, null, snapshot.val()); 
		}
	}, function(error) {
		if(callback) {
			callback(false, error);
		}
		else {
			utilities.sendResponse(response, error, null); 
		}
		
	});
};