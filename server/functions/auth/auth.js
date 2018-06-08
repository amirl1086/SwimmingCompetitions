
const firebase = require('firebase');
const admin = require('firebase-admin');
const utilities = require('./../utils/utils.js');


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
		    			addNewUser({ 'uid': currentUid }, userParams, (success, result) => { 
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
			addNewUser(userRecord, params, (success, result) => { 
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

	getUsersByParentId: function (params, response) {
		getCollectionByFilter('users', params.filters, params.value, (success, result) => {
			if(success) {
				utilities.sendResponse(response, null, result);
			}
			else {
				utilities.sendResponse(response, result, null);
			}
		});
	},

	addChildToParent: (params, response) => {
		let db = admin.database();
		let birthDate = params.birthDate;
		let email = params.email;

		//get users that match the selected email
		getCollectionByFilter('users', 'email', params.email, (success, result) => {
			let users = result;
			console.log('users ', users);
			if(!users) {
				utilities.sendResponse(response, {'message': 'no_such_email'}, null);
			}
			else {
				let user = users[Object.keys(users)[0]];
				console.log('params.birthDate' + params.birthDate);
				if(user.birthDate === params.birthDate) {
					user.parentId = params.uid;
					let userRef = db.ref('users/' + user.uid);
					userRef.update(user);
					userRef.on('value', (snapshot) => {
						utilities.sendResponse(response, null, snapshot.val());
					}, (error) => {
						utilities.sendResponse(response, error, null);
					})
				}
				else {
					utilities.sendResponse(response, {'message': 'birth_date_dont_match'}, null);
				}
			}
		});
	}

};


let getCollectionByFilter = (collectionName, filter, value, callback) => {
	let db = admin.database();
	let collectionRef = db.ref(collectionName);

	collectionRef.orderByChild(filter).equalTo(value).on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}

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

let addNewUser = (firebaseUser, userParams, callback) => {
	let db = admin.database();
	let usersRef = db.ref('users/' + firebaseUser.uid);

	//create the user object
	usersRef.set({
		'uid': firebaseUser.uid,
		'email': userParams.email,
		'firstName': userParams.firstName,
		'lastName': userParams.lastName,
		'birthDate': userParams.birthDate || '',
		'gender': userParams.gender || '',
		'type': userParams.type || '' //can be 'parent', 'student' or 'coach'
	});

	//insert to database
	usersRef.on('value', (snapshot) => {
		if(userParams.joinToCompetition) { //if join to competition is requested after registering
			joinToCompetition(snapshot.val(), (success, result) => {
				callback(success, snapshot.val());
			});
		}
		else {
			callback(true, snapshot.val());
		}
	}, (error) => {
		callback(false, error);
	});
};
