
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

		userRef.on('value', function(snapshot) {
			//add the uid to the currentUser
			callback(attachUidToUser(snapshot));
		}, function(error) {
			callback(error);
		});
	},

	setNewCompetition: function(competitionParams, response) {
		var db = admin.database();
		var newCompetitionKey = db.ref('competitions').push().key;

		var newCompetitionRef = db.ref('competitions/' + newCompetitionKey);
		console.log('competitionParams ', competitionParams);

		newCompetitionRef.update({
			'name': competitionParams.name,
			'activityDate': competitionParams.activityDate,
			'swimmingStyle': competitionParams.swimmingStyle,
			'numOfParticipants': competitionParams.numOfParticipants,
			'length': competitionParams.length,
			'toAge': competitionParams.toAge,
			'fromAge': competitionParams.fromAge
		});

		newCompetitionRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, attachIdToObject(snapshot));
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	getCompetitions: function(uid, response) {
		var db = admin.database();
		var competitionsRef = db.ref('competitions/');

		competitionsRef.on('value', function(snapshot) {
			console.log('getCompetitions ', snapshot.val());
			utilities.sendResponse(response, null, snapshot.val());
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},
	
	joinToCompetition: function(params, response) {
		var db = admin.database();
		var competitionsRef = db.ref('competitions/' + params.competitionId + '/participants').push();

		competitionsRef.set({
			'firstName': params.firstName,
			'lastName': params.lastName,
			'birthDate': params.birthDate,
			'gender': params.gender,
			'competed': params.competed,
			'score': params.score
		});

		competitionsRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, attachIdToObject(snapshot));
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	setCompetitionResults: function(params, response) {
		var db = admin.database();
		var participantsResults = JSON.parse(params.competition.participants);
		var personalResults = [];

		for(key in participantsResults) {
			var currentParticipant = JSON.parse(participantsResults[key]);
			var personalResult = {
				'id': currentParticipant.id,
				'firstName': currentParticipant.firstName,
				'lastName': currentParticipant.lastName,
				'birthDate': currentParticipant.birthDate,
				'gender': currentParticipant.gender,
				'score': currentParticipant.score
			}
			personalResults.push(personalResult);
		}
		
		var presonalResultsRef = db.ref('personalResults/' + params.id + '/').push();

		presonalResultsRef.set(personalResults);

		presonalResultsRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, snapshot.val());
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	}
	

};

var attachUidToUser = function(snapshot) {
	var currentUser = snapshot.val();
	var userWithUid = Object.assign({}, currentUser, { 'uid': snapshot.key }); //add the uid to the currentUser
	return userWithUid;
}

var attachIdToObject = function(snapshot) {
	var resObj = snapshot.val();
	var resObjId = Object.assign({}, resObj, { 'id': snapshot.key }); //add the id to the object
	return resObjId;
}