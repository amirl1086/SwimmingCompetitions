
const admin = require('firebase-admin');
var utilities = require('./utils.js');
var authentication = require('../auth/auth.js');
var moment = require('moment');

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

		var newCompetition = {
			'name': competitionParams.name,
			'activityDate': competitionParams.activityDate,
			'swimmingStyle': competitionParams.swimmingStyle,
			'numOfParticipants': competitionParams.numOfParticipants,
			'length': competitionParams.length,
			'toAge': competitionParams.toAge,
			'fromAge': competitionParams.fromAge
		};

		var newCompetitionKey;
		if(competitionParams.id) {
			newCompetitionKey = competitionParams.id;
		}
		else {
			newCompetitionKey = db.ref('competitions').push().key;
		}

		var newCompetitionRef = db.ref('competitions/' + newCompetitionKey);
		console.log('competitionParams ', competitionParams);

		newCompetitionRef.update(newCompetition);

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

	initCompetitionForIteration: function(params, response) {
		var db = admin.database();
		var competitionsRef = db.ref('competitions/' + params.competitionId);

		competitionsRef.on('value', function(snapshot) {
			var competition = snapshot.val();
			competition.currentParticipants = [];

			for(var i = 0; i < competition.numOfParticipants; i++) {
				competition.currentParticipants.push(competition.participants[i]);
				competition.participants[i].competed = true;
			}

			utilities.sendResponse(response, null, snapshot.val());
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	getPersonalResults: function(params, response) {
		var uid = params.uid;
		var db = admin.database();

		authentication.getUser(uid, null, function(currentUser) {
			var personalResultsRef = db.ref('personalResults/');

			personalResultsRef.on('value', function(snapshot) {
				if(currentUser.type === 'coach') {
					utilities.sendResponse(response, null, snapshot.val());
				}
				else {
					var competitions = snapshot.val();
					var selectedCompetitions = [];
					for(competitionId in competitions) {
						if(competitions[competitionId].child(uid).exists()) {
							selectedCompetitions.push(competitions[competitionId]);
						}
					}
					utilities.sendResponse(response, null, selectedCompetitions);
				}
			});
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

		updateCompetition(currentCompetition, function(competition) {
			//var participantsResults = JSON.parse(currentCompetition.participants);
			var participantsResults = JSON.parse(competition.currentParticipants);

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
			
			var presonalResultsRef = db.ref('personalResults/' + competition.id + '/');

			presonalResultsRef.update(personalResults);

			presonalResultsRef.on('value', 
				function(snapshot) {
					var competedParticipants = snapshot.val();

					var participants = competition.participants;
					var newParticipants = {};

					var numOfParticipants = 0;
					for(key in participants) {
						var newParticipant = participants[key];
						if(!competedParticipants[key] && numOfParticipants < parseInt(competition.numOfParticipants)) {

							newParticipants[key] = newParticipant;
							numOfParticipants++;
						}
					}

					if(Object.keys(newParticipants).length === 0) {
						//TODO - maybe needs to query all results
						var resultsAgeMap = sortPersonalResults(competition, snapshot.val());
						resultsAgeMap.type = 'resultsMap';
						utilities.sendResponse(response, null, resultsAgeMap);
					}
					else {
						competition.currentParticipants = newParticipants;
						console.log('competition ', competition);
						competition.type = 'newIteration';
						utilities.sendResponse(response, null, competition);
					}
					
				}, function(error) {
					utilities.sendResponse(response, error, null);
				}
			);
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
	var today = moment(new Date());
	//map results by age
	var resultsMap = Object.keys(results).reduce(function(totalResults, key) {
		var personalResult = results[key];

		var compare = moment(personalResult.birthDate);
		var participantAge = Math.round(today.diff(compare, 'years', true));

		if(!totalResults[participantAge]) {
			totalResults[participantAge] = { 'males' : [], 'females' : [] };
		}
		personalResult.userId = key;
		personalResult.gender === 'זכר' ? totalResults[participantAge].males.push(personalResult) : totalResults[participantAge].females.push(personalResult);
		return totalResults;
	}, {});

	//order results by gender
	Object.keys(resultsMap).forEach(function(resultsByAge) {
		var currentAgeResults = resultsMap[resultsByAge];

		arraySortByScore(currentAgeResults.males);
		arraySortByScore(currentAgeResults.females);
	});
	return resultsMap;
}

var arraySortByScore = function(arrayList) {
	var today = moment(new Date());
	arrayList.sort(function(itemA, itemB) {
	    if (parseFloat(itemA.score) < parseFloat(itemB.score)) {
	        return -1;
	    }
	    else if (parseFloat(itemA.score) > parseFloat(itemB.score)) {
	        return 1;
	    }
	    return 0;
	});
}

var updateCompetition = function(competition, callback) {
	var db = admin.database();
	var competitionsRef = db.ref('competitions/' + competition.id + '/');

	console.log('competition ', competition);
	competition.participants = JSON.parse(competition.participants);

	for(key in competition.participants) {
		competition.participants[key] = JSON.parse(competition.participants[key]);
	}
	delete competition.currentCompetition;

	competitionsRef.update(competition);

	competitionsRef.on('value', function(snapshot) {
		callback(snapshot.val());
	}, function(error) {
		callback(null);
	});
}