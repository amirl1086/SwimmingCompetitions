
const admin = require('firebase-admin');

var utilities = require('./utils.js');


module.exports = {

	addNewUser: function(firebaseUser, userParams, response) {
		var db = admin.database();
		var uid = firebaseUser.getToken();

		var usersRef = db.ref('users/' + firebaseUser.uid);
		usersRef.set({
			'firstName': userParams.firstName,
			'lastName': userParams.lastName,
			'birthDate': userParams.birthDate,
			'email': userParams.email,
			'gender': userParams.gender,
			'type': userParams.type //can be 'parent', 'student' or 'couch'
		});

		usersRef.on('value', function(snapshot) {
			console.log('addNewUser usersRef.on.value success ', snapshot.val());

		  	utilities.sendResponse(response, null, snapshot.val());
		}, function (error) {
			console.log('addNewUser usersRef.on.value error ', error);

		  	utilities.sendResponse(response, error, null);
		});

	}

}