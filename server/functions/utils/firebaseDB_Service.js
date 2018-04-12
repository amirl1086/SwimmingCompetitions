
const admin = require('firebase-admin');
var utilities = require('./utils.js');
var filters = require('./filters.js');
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
		//console.log('competitionParams ', competitionParams);

		newCompetitionRef.update(newCompetition);

		newCompetitionRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, attachIdToObject(snapshot));
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},


	getCompetitions: function(params, response) {
		var db = admin.database();
		var competitionsRef = db.ref('competitions/');

		competitionsRef.on('value', function(snapshot) {
			//console.log('getCompetitions ', snapshot.val());

			var result;
			if(params.filters) {
				result = filterCompetitions(snapshot.val(), params); 
			}
			else {
				result = snapshot.val();
			}

			utilities.sendResponse(response, null, result);
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

	getPersonalResultsByCompetitionId: function(params, response) {
		var competition = JSON.parse(params.competition);
		var competitionId = competition.id;
		var db = admin.database();

		var personalResultsRef = db.ref('personalResults/' + competitionId);

		personalResultsRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, snapshot.val());
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
		var currentCompetition = JSON.parse(params.competition);
		console.log('setCompetitionResults currentCompetition ', currentCompetition);

		updateCompetitionIteration(currentCompetition, function(competition) {
			console.log('setCompetitionResults competition ', competition);
			var participantsResults = competition.currentParticipants;

			updateCompetitionResults(participantsResults, competition.id, function(success, competedParticipants) {
				if(success) {
					console.log('setCompetitionResults competition.participants ', competition.participants);
					var participants = filters.filterCompetedParticipants(competition.participants);

					if(Object.keys(participants).length === 0) {
						currentCompetition.isDone = true;
						//updateCompetition(currentCompetition);
						//TODO - maybe needs to query all results
						var resultsAgeMap = sortPersonalResults(competition, competedParticipants);
						resultsAgeMap.type = 'resultsMap';
						utilities.sendResponse(response, null, resultsAgeMap);
					}
					else {
						competition.participants = participants;
						var sortedParticipants = filters.sortParticipantsByAge(competition.participants);
						filters.removeBlankSpots(competition, sortedParticipants);
						competition.currentParticipants = getNewParticipants(competition, sortedParticipants);

						console.log('competition ', competition);
						competition.type = 'newIteration';
						utilities.sendResponse(response, null, competition);
					}

					
				}
				else {
					utilities.sendResponse(response, result, null);
				}
			});
		});
	},

	initCompetitionForIterations: function(params, response) {
		/*var competition = JSON.parse(params.competition);
		console.log('initCompetitionForIterations competition ', competition);

		var participants = JSON.parse(competition.participants);
		console.log('initCompetitionForIterations participants ', participants);

		var currentParticipants = JSON.parse(competition.currentParticipants || '{}');
		console.log('initCompetitionForIterations currentParticipants ', currentParticipants);

		competition.participants = filters.filterCompetedParticipants(participants);
		console.log('competition.participants ', competition.participants);

		var sortedParticipants = filters.sortParticipantsByAge(competition.participants);
		console.log('sortedParticipants ', sortedParticipants);

		//filters.removeBlankSpots(competition, sortedParticipants);
		if(!Object.keys(currentParticipants).length) {
			competition.currentParticipants = getNewParticipants(competition, sortedParticipants);
		}
		else {
			competition.currentParticipants = currentParticipants;
		}
		competition.currentParticipants = currentParticipants;*/
		
		//utilities.sendResponse(response, null, competition);

		getCompetitionById(params.competitionId, function(success, result) {
			if(success) {
				var competition = result;
				console.log('initCompetitionForIterations competition ', competition);
				//console.log('competition ', JSON.stringify(competition));
				var participants = filters.filterCompetedParticipants(competition.participants);
				console.log('competition.participants ', participants);
				var sortedParticipants = filters.sortParticipantsByAge(participants);
				console.log('sortedParticipants ', sortedParticipants);

				//console.log('sortedParticipants ', JSON.stringify(sortedParticipants));

				filters.removeBlankSpots(competition, sortedParticipants);
				console.log('sortedParticipants ', sortedParticipants);
				competition.currentParticipants = getNewParticipants(competition, sortedParticipants);
				console.log('currentParticipants ', competition.currentParticipants);
				utilities.sendResponse(response, null, competition);
			}
			else {
				utilities.sendResponse(response, result, null);
			}

		});
	}
};

var updateCompetitionResults = function(participantsResults, competitionId, callback) {
	var db = admin.database();
	var presonalResultsRef = db.ref('personalResults/' + competitionId + '/');

	console.log('updateCompetitionResults participantsResults ', participantsResults);
	console.log('updateCompetitionResults competitionId ', competitionId);

	var personalResults = {};
	for(var key in participantsResults) {
		var currentParticipant = participantsResults[key];
		personalResults[currentParticipant.id] = {
			'firstName': currentParticipant.firstName,
			'lastName': currentParticipant.lastName,
			'birthDate': currentParticipant.birthDate,
			'gender': currentParticipant.gender,
			'score': currentParticipant.score
		};
	}
	
	presonalResultsRef.update(personalResults);
	presonalResultsRef.on('value', function(snapshot) {
		console.log('updateCompetitionResults snapshot ', snapshot.val());
		callback(true, snapshot.val());
	}, function(error) {
		callback(false, error);
	});
}

var getNewParticipants = function(competition, sortedParticipants) {
	var newParticipants;
	var currentAge = parseInt(competition.toAge);
	var fromAge = parseInt(competition.fromAge);
	console.log('fromAge ', fromAge);

	while(currentAge >= fromAge) {
		console.log('currentAge ', currentAge);

		if(sortedParticipants[currentAge.toString()]) {
			console.log('sortedParticipants[currentAge.toString()] ', JSON.stringify(sortedParticipants[currentAge.toString()]));

			if(sortedParticipants[currentAge.toString()].males.length) {
				newParticipants = getNewParticipantsFromGendger(sortedParticipants[currentAge.toString()].males, parseInt(competition.numOfParticipants));
				if(Object.keys(newParticipants).length) {
					break;
				}
			}
			if(sortedParticipants[currentAge.toString()].females.length) {
				newParticipants = getNewParticipantsFromGendger(sortedParticipants[currentAge.toString()].females, parseInt(competition.numOfParticipants));
				if(Object.keys(newParticipants).length) {
					break;
				}
			}

		}
		currentAge--;
	}
	console.log('newParticipants ', newParticipants);
	return newParticipants;
}


var getNewParticipantsFromGendger = function(participants, numOfParticipants) {
	var newParticipants = {};
	var totalSelected = 0;
	//console.log('getNewParticipantsFromGendger participants ', JSON.stringify(participants));
	//console.log('getNewParticipantsFromGendger numOfParticipants ', numOfParticipants);
	for(var i = 0; i < participants.length; i++) {
		if(participants[i].competed === 'false' && totalSelected < numOfParticipants) {
			newParticipants[participants[i].id] = participants[i];
			totalSelected++
		}
	}
	console.log('getNewParticipantsFromGendger newParticipants ', newParticipants)
	return newParticipants;
}

var attachUidToUser = function(snapshot) {
	var currentUser = snapshot.val();
	var userWithUid = Object.assign({}, currentUser, { 'uid': snapshot.key }); //add the uid to the currentUser
	return userWithUid;
}

var getCompetitionById = function(competitionId, callback) {
	var db = admin.database();
	var competitionsRef = db.ref('competitions/' + competitionId);

	competitionsRef.on('value', function(snapshot) { 
		callback(true, snapshot.val());
	}, function(error) {
		callback(false, error);
	});
}

var attachIdToObject = function(snapshot) {
	var resObj = snapshot.val();
	var resObjId = Object.assign({}, resObj, { 'id': snapshot.key }); //add the id to the object
	return resObjId;
}

var sortPersonalResults = function(currentCompetition, results) {
	var today = moment(new Date());
	//map results by age
	var resultsMap = filters.sortParticipantsByAge(results);

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

var updateCompetitionIteration = function(competition, callback) {
	var db = admin.database();
	var competitionsRef = db.ref('competitions/' + competition.id + '/');

	//console.log('updateCompetitionIteration competition ', competition);
	competition.participants = JSON.parse(competition.participants);
	competition.currentParticipants = JSON.parse(competition.currentParticipants);

	for(var key in competition.participants) {
		console.log('competition.participants[key] ', competition.participants[key]);
		competition.participants[key] = competition.participants[key];
		var competedParticipant = competition.currentParticipants[key];
		if(competedParticipant) {
			competition.participants[key].competed = 'true';
		}
	}
	//delete competition.currentParticipants;

	competitionsRef.update(competition);

	competitionsRef.on('value', function(snapshot) {
		callback(snapshot.val());
	}, function(error) {
		callback(null);
	});
}

var updateCompetition = function(competition) {
	var db = admin.database();
	var competitionsRef = db.ref('competitions/' + competition.id + '/');
	competitionsRef.update(competition);
}

var filterCompetitions = function(competitions, params) {
	var currentUser = JSON.parse(params.currentUser);
	var today = moment();
	var birthDate = moment(currentUser.birthDate);
	var userAge = Math.floor(today.diff(birthDate, 'years', true));
	var filters = params.filters.split(',');

	var filteredCompetitions = {};

	//console.log('competitions ',  competitions);
	//console.log('params ',  params);

	for(var key in competitions) {
		var currentCompetition = competitions[key];
		console.log('currentCompetition ',  currentCompetition);

		for(var i = 0; i < filters.length; i++) {
			if(filters[i] === 'uid') {
				if(searchInParticipants(currentCompetition, currentUser.uid)) {
					filteredCompetitions[key] = competitions[key];
				}
			}
			else if(filters[i] === 'age') {
				var fromAge = parseInt(currentCompetition.fromAge);
				var toAge = parseInt(currentCompetition.toAge);
				if(userAge >= fromAge && userAge <= toAge) {
					filteredCompetitions[key] = competitions[key];
				}
			}
			else if(filters[i] === 'results') {
				if(currentCompetition.isDone) {
					filteredCompetitions[key] = competitions[key];
				}
			}
		}
	}

	return filteredCompetitions;
}

var searchInParticipants = function(competition, uid) {
	for(var key in competition.participants) {
		if(key === uid) {
			return true;
		}
	}
	return false;
}