
const admin = require('firebase-admin');

var utilities = require('./utils.js');

var moment = require('moment');

console.log('moment ', moment);


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
			console.log('firebase finished inserting new user ', snapshot)
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
		var competitionsRef, newParticipant = {};

		if(params.uid) {
			competitionsRef = db.ref('competitions/' + params.competitionId + '/participants/' + params.uid);
		}
		else {
			competitionsRef = db.ref('competitions/' + params.competitionId + '/participants').push();

			newParticipant = {
				'firstName': params.firstName,
				'lastName': params.lastName,
				'birthDate': params.birthDate,
				'gender': params.gender
			};
		}

		newParticipant = Object.assign({
			'score': params.score || '0', 
			'competed': params.competed || 'false'
		}, newParticipant);

		competitionsRef.set(newParticipant);

		competitionsRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, attachIdToObject(snapshot));
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	setCompetitionResults: function(params, response) {
		var db = admin.database();
		var currentCompetition = JSON.parse(params.competition);
		var participantsResults = JSON.parse(currentCompetition.participants);

		var personalResults = {};

		for(key in participantsResults) {
			var currentParticipant = JSON.parse(participantsResults[key]);
			personalResults[currentParticipant.id] = {
				'firstName': currentParticipant.firstName,
				'lastName': currentParticipant.lastName,
				'birthDate': currentParticipant.birthDate,
				'gender': currentParticipant.gender,
				'score': currentParticipant.score
			};
		}
		
		var presonalResultsRef = db.ref('personalResults/' + currentCompetition.id + '/');

		presonalResultsRef.set(personalResults);

		presonalResultsRef.on('value', function(snapshot) {
			var results = snapshot;

			console.log('setCompetitionResults snapshot ', snapshot.val());

			var resultsAgeMap = sortPersonalResults(currentCompetition, results);
			utilities.sendResponse(response, null, resultsAgeMap);
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

var sortPersonalResults = function(currentCompetition, results) {

	//map results by age
	var resultsMap = Object.keys(results).reduce(function(totalResults, key) {
		console.log('totalResults ', totalResults);
		console.log('key ', key);
		var personalResult = results[key];
		console.log('personalResult ', personalResult);
		var participantAge = Math.floor(moment(new Date()).diff(personalResult.birthDate,"DD/MM/YYYY"), 'years', true);
		console.log('participantAge ', participantAge);
		if(!totalResults[participantAge]) {
			totalResults[participantAge] = { 'males' : [], 'females' : [] };
		}
		console.log('personalResult.gender ', personalResult.gender);
		personalResult.gender === 'זכר' ? totalResults[participantAge].males.push(personalResult) : totalResults[participantAge].females.push(personalResult);
		return totalResults;
	}, {});

	//order results by gender
	Object.keys(resultsMap).forEach(function(resultsByAge) {
		console.log('resultsByAge ', resultsByAge);
		var currentAgeResults = resultsMap[resultsByAge];
		arraySortByScore(currentAgeResults.males);
		arraySortByScore(currentAgeResults.females);
	});
	return resultsMap;
}

var arraySortByScore = function(arrayList) {
	var today = moment(new Date());
	arrayList.sort(function(itemA, itemB) {
		itemAage = Math.floor(today.diff(itemA.birthDate, "DD/MM/YYYY"), 'years', true);
		itemBage = Math.floor(today.diff(itemB.birthDate, "DD/MM/YYYY"), 'years', true);
		console.log('itemAage ', itemAage);
		console.log('itemBage ', itemBage);
	    if (itemAage < itemBage) {
	        return -1;
	    }
	    else if (itemAage > itemBage) {
	        return 1;
	    }
	    return 0;
	});
}