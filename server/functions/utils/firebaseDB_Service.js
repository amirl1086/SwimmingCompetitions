
const admin = require('firebase-admin');

var utilities = require('./utils.js');


module.exports = {

	addNewUser: function(firebaseUser, userParams, callback) {
		var db = admin.database();
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
			console.log('firebase finished inserting new user')
			//add the uid to the currentUser
			callback(attachUidToUser(snapshot));
		});
	},

	getUser: function(uid, callback) {
		var db = admin.database();
		var userRef = db.ref('users/' + uid);

		userRef.on("value", function(snapshot) {
			//add the uid to the currentUser
			callback(attachUidToUser(snapshot));
		});
	},

	setNewCompetition: function(competitionParams, callback) {
		var db = admin.database();
		var userRef = db.ref('competitions');

		userRef.on('value', function(snapshot) {
			callback(snapshot.val());
		});
	}

	

};

var attachUidToUser = function(snapshot) {
	var currentUser = snapshot.val();

	//add the uid to the currentUser
	var userWithUid = Object.assign({}, snapshot.val(), { 'uid': snapshot.key });
	return userWithUid;
}