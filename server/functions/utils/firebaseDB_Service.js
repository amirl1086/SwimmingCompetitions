
const admin = require('firebase-admin');
const utilities = require('./utils.js');
const filters = require('./filters.js');
const authentication = require('../auth/auth.js');
console.log('authentication 1 ', authentication);
const moment = require('moment');

module.exports = {

	addNewUser: function(firebaseUser, userParams, callback) {
		let db = admin.database();
		let usersRef = db.ref('users/' + firebaseUser.uid);

		usersRef.set({
			'uid': firebaseUser.uid,
			'email': userParams.email,
			'firstName': userParams.firstName,
			'lastName': userParams.lastName,
			'birthDate': userParams.birthDate || '',
			'gender': userParams.gender || '',
			'type': userParams.type || '' //can be 'parent', 'student' or 'coach'
		});

		usersRef.on('value', function(snapshot) {
			if(userParams.joinToCompetition) {
				joinToCompetition(snapshot.val(), function(success, result) {
					callback(success, snapshot.val());
				});
			}
			else {
				callback(true, snapshot.val());
			}
		}, function(error) {
			callback(false, error);
		});
	},

	addChildToParent: function(params, response) {
		console.log('params ', params);
		let birthDate = params.birthDate;
		let email = params.email;
		getCollectionByFilter('users', 'email', params.email, function(success, result) {
			let users = result;
			console.log('users ', users);
			if(!Object.keys(users).length) {
				utilities.sendResponse(response, 'no_such_email', null);
			}
			else {
				let user = users[Object.keys(users)[0]];
				if(user.birthDate === params.birthDate) {
					utilities.sendResponse(response, null, child);
				}
				else {
					utilities.sendResponse(response, 'birth_date_dont_match', null);
				}
			}
		});
	},

	updateFirebaseUser: function(params, response) {
		admin.auth().updateUser(params.uid, { displayName: params.firstName + ' ' + params.lastName }).then(function(userRecord) {
		    let db = admin.database();
			let usersRef = db.ref('users/' + params.uid);

			usersRef.update({
				'firstName': params.firstName,
				'lastName': params.lastName,
				'birthDate': params.birthDate || '',
				'gender': params.gender || '',
				'type': params.type
			});

			usersRef.on('value', function(snapshot) {
				utilities.sendResponse(response, null, snapshot.val());
			}, function(error) {
				utilities.sendResponse(response, error, null);
			});
		})
		.catch(function(error) {
		    console.log("Error updating user:", error);
		});
	},

	addExistingUserToCompetition: function(params, response) {
		console.log('addExistingUserToCompetition params ', params);
		getCollectionByFilter('users', 'email', params.email, function(success, result) {
			let users = result;
			console.log('users ', users);
			if(!Object.keys(users).length) {
				utilities.sendResponse(response, 'no_such_email', null);
			}
			else {
				let user = users[Object.keys(users)[0]];
				if(user.birthDate === params.birthDate) {
					params.uid = user.uid;
					joinToCompetition(params, function(success, result2) {
						if(success) {
							utilities.sendResponse(response, null, result2);
						} 
						else {
							utilities.sendResponse(response, result2, null);
						}
					});
				}
				else {
					utilities.sendResponse(response, 'birth_date_dont_match', null);
				}
			}
		});
	},

	getUser: function(uid, callback) {
		let db = admin.database();
		let userRef = db.ref('users/' + uid);

		userRef.on('value', function(snapshot) {
			callback(true, snapshot.val());
		}, function(error) {
			callback(false, error);
		});
	},

	getUsersByFilters: function(params, response) {
		let db = admin.database();
		let usersRef = db.ref('users');

		usersRef.orderByChild(params.filter).equalTo(params.value).on('value', function(snapshot) {
			utilities.sendResponse(response, null, snapshot.val());
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	setNewCompetition: function(competitionParams, response) {
		let db = admin.database();

		let newCompetition = {
			'name': competitionParams.name,
			'activityDate': competitionParams.activityDate,
			'swimmingStyle': competitionParams.swimmingStyle,
			'numOfParticipants': competitionParams.numOfParticipants,
			'length': competitionParams.length,
			'toAge': competitionParams.toAge,
			'fromAge': competitionParams.fromAge
		};

		let newCompetitionKey;
		if(competitionParams.id) {
			newCompetitionKey = competitionParams.id;
		}
		else {
			newCompetitionKey = db.ref('competitions').push().key;
		}

		let newCompetitionRef = db.ref('competitions/' + newCompetitionKey);
		//console.log('competitionParams ', competitionParams);

		newCompetitionRef.update(newCompetition);

		newCompetitionRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, attachIdToObject(snapshot));
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	getCompetitions: function(params, response) {
		let db = admin.database();
		let competitionsRef = db.ref('competitions/');

		competitionsRef.on('value', function(snapshot) {
			let result;
			if(params.filters) {
				result = filters.filterCompetitions(snapshot.val(), params); 
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
		let uid = params.uid;
		let db = admin.database();
		authentication.getUser(uid, null, function(sucess, result) {
			if(sucess) {
				let personalResultsRef = db.ref('personalResults/');
				let currentUser = result;
				personalResultsRef.on('value', function(snapshot) {
					if(currentUser.type === 'coach') {
						utilities.sendResponse(response, null, snapshot.val());
					}
					else {
						let competitions = snapshot.val();
						let selectedCompetitions = [];
						for(competitionId in competitions) {
							if(competitions[competitionId].child(uid).exists()) {
								selectedCompetitions.push(competitions[competitionId]);
							}
						}
						utilities.sendResponse(response, null, selectedCompetitions);
					}
				});
			}
			else {
				utilities.sendResponse(response, result, null);
			}
		});
	},

	getPersonalResultsByCompetitionId: function(params, response) {
		let competition = JSON.parse(params.competition);
		let competitionId = competition.id;

		getCompetitionResults(competition, function(success, result) {
			if(success) {
				let resultsAgeMap = sortPersonalResults(competition, result);
				utilities.sendResponse(response, null, resultsAgeMap);
			}
			else {
				utilities.sendResponse(response, result, null);
			}
			
		});
	},
	
	joinToCompetition: function(params, response) {
		joinToCompetition(params, function(success, result) {
			if(success) {
				utilities.sendResponse(response, null, result);
			} 
			else {
				utilities.sendResponse(response, result, null);
			}
		});
	},

	cancelRegistration: function(params, response) {
		let db = admin.database();
		competitionsRef = db.ref('competitions/' + params.competitionId + '/participants/' + params.uid);
		competitionsRef.remove();

		competitionsRef.on('value', function(snapshot) {
			utilities.sendResponse(response, null, snapshot.val());
		}, function(error) {
			utilities.sendResponse(response, error, null);
		});
	},

	setCompetitionResults: function(params, response) {
		let currentCompetition = JSON.parse(params.competition);
		//console.log('setCompetitionResults currentCompetition ', currentCompetition);

		updateCompetitionIteration(currentCompetition, function(competition) {
			//console.log('setCompetitionResults competition ', competition);
			let participantsResults = competition.currentParticipants;

			updateCompetitionResults(participantsResults, competition.id, function(success, competedParticipants) {
				if(success) {
					//console.log('setCompetitionResults competition.participants ', competition.participants);
					let participants = filters.filterCompetedParticipants(competition.participants);

					if(Object.keys(participants).length === 0) {
						currentCompetition.isDone = 'true';
						//updateCompetition(currentCompetition);
						//TODO - maybe needs to query all results
						let resultsAgeMap = sortPersonalResults(competition, competedParticipants);
						resultsAgeMap.type = 'resultsMap';
						utilities.sendResponse(response, null, resultsAgeMap);
					}
					else {
						//competition.participants = participants;
						let sortedParticipants = filters.sortParticipantsByAge(participants);
						//console.log('setCompetitionResults sortedParticipants ', sortedParticipants);
						filters.removeBlankSpots(competition, sortedParticipants);
						//console.log('setCompetitionResults removeBlankSpots sortedParticipants ', sortedParticipants);
						competition.currentParticipants = getNewParticipants(competition, sortedParticipants);
						//console.log('setCompetitionResults currentParticipants ', competition.currentParticipants);

						//console.log('competition ', competition);
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

		getCompetitionById(params.competitionId, function(success, result) {
			if(success) {
				let competition = result;
				let newParticipants = filters.filterCompetedParticipants(competition.participants);

				if(!Object.keys(newParticipants).length && Object.keys(competition.participants).length) {
					getCompetitionResults(competition, function(success, result) {
						if(success) {
							let resultsAgeMap = sortPersonalResults(competition, result);
							resultsAgeMap.type = 'resultsMap';
							utilities.sendResponse(response, null, resultsAgeMap);
						}
						else {
							utilities.sendResponse(response, result, null);
						}
					});
				}
				else {
					let sortedParticipants = filters.sortParticipantsByAge(newParticipants);
					filters.removeBlankSpots(competition, sortedParticipants);
					competition.currentParticipants = getNewParticipants(competition, sortedParticipants);
					competition.type = 'newIteration';
					utilities.sendResponse(response, null, competition);
				}
			}
			else {
				utilities.sendResponse(response, result, null);
			}

		});
	}
};

let getCollectionByFilter = function(collectionName, filter, value, callback) {
	console.log('getCollectionByFilter params collectionName: ', collectionName, ', filter: ', filter, ', value: ', value);
	let db = admin.database();
	let collectionRef = db.ref(collectionName);

	collectionRef.orderByChild(filter).equalTo(value).on('value', function(snapshot) {
		callback(true, snapshot.val());
	}, function(error) {
		callback(false, error);
	});
}

let joinToCompetition = function(params, callback) {
	let db = admin.database();
	let newParticipantUid;
	
	if(params.uid) {
		newParticipantUid = params.uid;
	}
	else {
		newParticipantUid = db.ref('competitions/' + params.competitionId + '/participants').push().key;
	}
	
	competitionsRef = db.ref('competitions/' + params.competitionId + '/participants/' + newParticipantUid);

	let newParticipant = {
		'firstName': params.firstName,
		'lastName': params.lastName,
		'birthDate': params.birthDate,
		'gender': params.gender,
		'uid': newParticipantUid,
		'score': params.score || '0', 
		'competed': 'false'
	};

	competitionsRef.set(newParticipant);

	competitionsRef.on('value', function(snapshot) {
		callback(true, snapshot.val());
	}, function(error) {
		callback(false, error);
	});
};

let updateCompetitionResults = function(participantsResults, competitionId, callback) {
	let db = admin.database();
	let presonalResultsRef = db.ref('personalResults/' + competitionId + '/');

	let personalResults = {};
	for(let key in participantsResults) {
		let currentParticipant = participantsResults[key];
		personalResults[currentParticipant.id] = {
			'firstName': currentParticipant.firstName,
			'lastName': currentParticipant.lastName,
			'birthDate': currentParticipant.birthDate,
			'gender': currentParticipant.gender,
			'score': currentParticipant.score,
			'uid': currentParticipant.id
		};
	}
	
	presonalResultsRef.update(personalResults);
	presonalResultsRef.on('value', function(snapshot) {
		callback(true, snapshot.val());
	}, function(error) {
		callback(false, error);
	});
}

let getCompetitionResults = function(competition, callback) {
	let db = admin.database();
	let presonalResultsRef = db.ref('personalResults/' + competition.id + '/');

	presonalResultsRef.on('value', function(snapshot) {
		if(!snapshot.val()) {
			createCompetitionResults(competition, function(success) {
				callback(success, snapshot.val());
			});	
		}
		else {
			callback(true, snapshot.val());
		}
	}, function(error) {
		callback(false, error);
	});
}

let getNewParticipants = function(competition, sortedParticipants) {
	let newParticipants;
	let currentAge = parseInt(competition.toAge);
	let fromAge = parseInt(competition.fromAge);

	while(currentAge >= 0) {
		if(sortedParticipants[currentAge.toString()]) {
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
	return newParticipants;
}


let getNewParticipantsFromGendger = function(participants, numOfParticipants) {
	let newParticipants = {};
	let totalSelected = 0;
	for(let i = 0; i < participants.length; i++) {
		if((!participants[i].competed || participants[i].competed === 'false') && totalSelected < numOfParticipants) {
			newParticipants[participants[i].id] = participants[i];
			totalSelected++
		}
	}
	return newParticipants;
}

let getCompetitionById = function(competitionId, callback) {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competitionId);

	competitionsRef.on('value', function(snapshot) { 
		callback(true, snapshot.val());

		
		/*let competition;
		competitionsRef = db.ref('competitions/-LAXsGT3ZyzpPsfjcmve');
		competitionsRef.on('value', function(snapshot2) { 
			competition = snapshot2.val();
			console.log('competition ', competition);
			//competition.participants = {};
			for(let key in competition.participants) {
				competition.participants[key].uid = competition.participants[key].id;
				delete competition.participants[key].id;
			}
			let key = db.ref('competitions').push().key;
			competitionsRef = db.ref('competitions/' + key);
			//console.log('competitionParams ', competitionParams);
			console.log('competition2 ', competition);
			competitionsRef.update(competition);
			//callback(true, snapshot1.val());
		});*/


	}, function(error) {
		callback(false, error);
	});
}

let attachIdToObject = function(snapshot) {
	let resObj = snapshot.val();
	let resObjId = Object.assign({}, resObj, { 'id': snapshot.key }); //add the id to the object
	return resObjId;
}

let sortPersonalResults = function(currentCompetition, results) {
	let today = moment(new Date());
	//map results by age
	let resultsMap = filters.sortParticipantsByAge(results);

	//order results by gender
	Object.keys(resultsMap).forEach(function(resultsByAge) {
		let currentAgeResults = resultsMap[resultsByAge];

		arraySortByScore(currentAgeResults.males);
		arraySortByScore(currentAgeResults.females);
	});
	return resultsMap;
}

let arraySortByScore = function(arrayList) {
	let today = moment(new Date());
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

let updateCompetitionIteration = function(competition, callback) {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competition.id + '/');

	competition.participants = JSON.parse(competition.participants);
	competition.currentParticipants = JSON.parse(competition.currentParticipants);

	for(let key in competition.participants) {
		let competedParticipant = competition.currentParticipants[key];
		if(competition.currentParticipants[key]) {
			competition.participants[key].score = competition.currentParticipants[key].score;
			competition.participants[key].competed = 'true';
		}
	}

	competitionsRef.update(competition);

	competitionsRef.on('value', function(snapshot) {
		callback(snapshot.val());
	}, function(error) {
		callback(null);
	});
}

let updateCompetition = function(competition) {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competition.id + '/');
	competitionsRef.update(competition);
}



let searchInParticipants = function(competition, uid) {
	for(let key in competition.participants) {
		if(key === uid) {
			return true;
		}
	}
	return false;
}

let createCompetitionResults = function(competition, callback) {
	let db = admin.database();
	let presonalResultsRef = db.ref('personalResults/' + competition.id + '/');

	let personalResults = {};
	for(let key in competition.participants) {
		let currentParticipant = competition.participants[key];
		personalResults[currentParticipant.id] = {
			'firstName': currentParticipant.firstName,
			'lastName': currentParticipant.lastName,
			'birthDate': currentParticipant.birthDate,
			'gender': currentParticipant.gender,
			'score': currentParticipant.score,
			'uid': currentParticipant.id
		};
	}
	
	presonalResultsRef.update(personalResults);
	presonalResultsRef.on('value', function(snapshot) {
		callback(true, snapshot.val());
	}, function(error) {
		callback(false, error);
	});
}