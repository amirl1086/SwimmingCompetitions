
const admin = require('firebase-admin');

var utilities = require('./utils.js');


module.exports = {

	addNewUser: function(firebaseUser, userParams, callback) {
		var db = admin.database();
		var uid = firebaseUser.getToken();
		var usersRef = db.ref('users/' + firebaseUser.uid);

		usersRef.set({
			'firstName': userParams.firstName,
			'lastName': userParams.lastName,
			'birthDate': userParams.birthDate,
			'email': userParams.email,
			'gender': userParams.gender,
			'type': userParams.type //can be 'parent', 'student' or 'coach'
		});

		usersRef.on('value', function(snapshot) {
			callback(snapshot.val());
		});
	},

	getUser: function(uid, callback) {
		var db = admin.database();
		var userRef = db.ref('users/' + uid);

		userRef.on("value", function(snapshot) {
			callback(snapshot.val());
		});
	},

	setNewCompetition: function(competitionParams, callback) {
		var db = admin.database();
		var userRef = db.ref('competitions');

		userRef.on('value', function(snapshot) {
			callback(snapshot.val());
		});
	}

}