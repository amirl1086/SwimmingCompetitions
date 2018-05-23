
const firebase = require('firebase');
const admin = require('firebase-admin');

const utilities = require('./../utils/utils.js');
const firebaseDB_Service = require('./../utils/firebaseDB_Service.js');

module.exports =  {

	logIn: (idToken, response) => {
		admin.auth().verifyIdToken(idToken).then((decodedToken) => {
			var currentUid = decodedToken.uid;
		    getUser(currentUid, null, (sucess, result) => {
		    	if(sucess) {
		    		if(!result) {
		    			var userParams = { 
		    				'firstName': decodedToken.name.substr(0, decodedToken.name.indexOf(' ')),
		    				'lastName': decodedToken.name.substr(decodedToken.name.indexOf(' ') + 1),
		    				'email': decodedToken.email,
		    				'uid': currentUid
		    			};
		    			firebaseDB_Service.addNewUser({ 'uid': currentUid }, userParams, (success, result) => { 
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
		.catch((error) => {
		  	utilities.sendResponse(response, error, null);
		});
	},

	getUser: (uid, response, callback) => {
		getUser(uid, response, callback);
	},

	addNewFirebaseUser: (params, response) => {
		//create new user in with credantials
		console.log('params ', params);
		var newUser = {
			'email': params.email,
		  	'password': params.password,
			'displayName': params.firstName + ' ' + params.lastName
		}

		admin.auth().createUser(newUser).then((userRecord) => {
			console.log('firebaseUser ', userRecord);
			//utilities.sendResponse(response, null, userRecord);
			firebaseDB_Service.addNewUser(userRecord, params, (success, result) => { 
				if(success) {
					utilities.sendResponse(response, null, result); 
				}
				else {
					utilities.sendResponse(response, result, null);
				}
			});
		})
		.catch((error) => {
			console.log('addNewFirebaseUser error: ', error);
			utilities.sendResponse(response, error, null);
		});
	},

	getFirebaseUser: (uid, callback) => {
		admin.auth().getUser(uid).then((result) => {
			callback(true, result);
		})
		.catch((error) => {
			callback(false, error);
		});
	},

	updateUserDetails: (params, response) => {

	}

};

let getUser = (uid, response, callback) => {
	let db = admin.database();
	let userRef = db.ref('users/' + uid);

	userRef.on('value', (snapshot) => {
		if(callback) {
			callback(true, snapshot.val());
		}
		else {
			utilities.sendResponse(response, null, snapshot.val()); 
		}
	}, (error) => {
		if(callback) {
			callback(false, error);
		}
		else {
			utilities.sendResponse(response, error, null); 
		}
		
	});
};