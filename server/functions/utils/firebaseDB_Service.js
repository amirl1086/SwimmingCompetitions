
const admin = require('firebase-admin');
const utilities = require('./utils.js');
const filters = require('./filters.js');
const authentication = require('../auth/auth.js');
const moment = require('moment');

module.exports = {

	addNewUser: (firebaseUser, userParams, callback) => {
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
				utilities.sendResponse(response, 'no_such_email', null);
			}
			else {
				let user = users[Object.keys(users)[0]];
				if(user.birthDate === params.birthDate) {
					let userRef = db.ref('users/' + params.uid + '/children/' + user.uid);
					userRef.set({
						"firstName": user.firstName
					});
					utilities.sendResponse(response, null, user);
				}
				else {
					utilities.sendResponse(response, 'birth_date_dont_match', null);
				}
			}
		});
	},

	updateFirebaseUser: (params, response) => {
		admin.auth().updateUser(params.uid, { displayName: params.firstName + ' ' + params.lastName }).then((userRecord) => {
		    let db = admin.database();
			let usersRef = db.ref('users/' + params.uid);

			usersRef.update({
				'firstName': params.firstName,
				'lastName': params.lastName,
				'birthDate': params.birthDate || '',
				'gender': params.gender || '',
				'type': params.type
			});

			usersRef.on('value', (snapshot) => {
				utilities.sendResponse(response, null, snapshot.val());
			}, (error) => {
				utilities.sendResponse(response, error, null);
			});
		})
		.catch((error) => {
		    console.log("Error updating user:", error);
		});
	},

	addExistingUserToCompetition: (params, response) => {
		console.log('addExistingUserToCompetition params ', params);
		getCollectionByFilter('users', 'email', params.email, (success, result) => {
			let users = result;
			console.log('users ', users);
			if(!users) {
				utilities.sendResponse(response, {'message': 'no_such_email'}, null);
			}
			else {
				let user = users[Object.keys(users)[0]];
				if(user.birthDate === params.birthDate) {
					params = Object.assign(params, user);
					joinToCompetition(params, (success, result2) => {
						if(success) {
							utilities.sendResponse(response, null, result2);
						} 
						else {
							utilities.sendResponse(response, result2, null);
						}
					});
				}
				else {
					utilities.sendResponse(response, {'message': 'birth_date_dont_match'}, null);
				}
			}
		});
	},

	getUsersByFilters: (params, response) => {
		let db = admin.database();
		let usersRef = db.ref('users');

		usersRef.orderByChild(params.filter).equalTo(params.value).on('value', (snapshot) => {
			utilities.sendResponse(response, null, snapshot.val());
		}, (error) => {
			utilities.sendResponse(response, error, null);
		});
	},

	setNewCompetition: (competitionParams, response) => {
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

		newCompetitionRef.on('value', (snapshot) => {
			utilities.sendResponse(response, null, attachIdToObject(snapshot));
		}, (error) => {
			utilities.sendResponse(response, error, null);
		});
	},

	getCompetitions: (params, response) => {
		let db = admin.database();
		let competitionsRef = db.ref('competitions/');

		competitionsRef.on('value', (snapshot) => {
			let result;
			if(params.filters) {
				result = filters.filterCompetitions(snapshot.val(), params); 
			}
			else {
				result = snapshot.val();
			}

			utilities.sendResponse(response, null, result);
		}, (error) => {
			utilities.sendResponse(response, error, null);
		});
	},

	getParticipantStatistics: (params, response) => {
		getCollectionByName('personalResults', (success, result) => {
			if(success) {
				let personalResults = result;
				let statisticsResults = [];
				let selectedCompetitions = new Set();
				console.log('getParticipantStatistics result ', personalResults);

				//create the array of scores by competition id
				for(let competitionId in personalResults) {
					let participants = personalResults[competitionId];
					if(participants[params.uid]) {
						selectedCompetitions.add(competitionId);
						statisticsResults.push({'competition': competitionId, 'score': participants[params.uid].score});
					}
				}

				//retreive the competitions data to assign it by styles and years
				getCollectionByName('competitions', (success, result) => {
					if(success) {
						//map to list and list to map to reduce runtime for searching 
						let competitions = result;
						//let filteredCompetitions = utilities.listToMap(competitions.filter((competition) => selectedCompetitions.has(competition.id)), 'id');

						//attach the competition object to the correct place in the array
						for(let i in statisticsResults) {
							statisticsResults[i].competition = competitions[statisticsResults[i].competition];
						}

						console.log('statisticsResults ', statisticsResults);
						utilities.sendResponse(response, null, statisticsResults);
					}
					else {
						utilities.sendResponse(response, result, null);
					}
				});
			}
			else {
				utilities.sendResponse(response, result, null);
			}

		})
	},

	getPersonalResults: (params, response) => {
		let uid = params.uid;
		let db = admin.database();
		authentication.getUser(uid, null, (sucess, result) => {
			if(sucess) {
				let personalResultsRef = db.ref('personalResults/');
				let currentUser = result;
				personalResultsRef.on('value', (snapshot) => {
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

	getPersonalResultsByCompetitionId: (params, response) => {
		let competition = JSON.parse(params.competition);
		let competitionId = competition.id;

		getCompetitionResults(competition, (success, result) => {
			if(success) {
				let resultsAgeMap = sortPersonalResults(competition, result);
				utilities.sendResponse(response, null, resultsAgeMap);
			}
			else {
				utilities.sendResponse(response, result, null);
			}
			
		});
	},
	
	joinToCompetition: (params, response) => {
		joinToCompetition(params, (success, result) => {
			if(success) {
				utilities.sendResponse(response, null, result);
			} 
			else {
				utilities.sendResponse(response, result, null);
			}
		});
	},

	cancelRegistration: (params, response) => {
		let db = admin.database();
		competitionsRef = db.ref('competitions/' + params.competitionId + '/participants/' + params.uid);
		competitionsRef.remove();

		competitionsRef.on('value', (snapshot) => {
			utilities.sendResponse(response, null, snapshot.val());
		}, (error) => {
			utilities.sendResponse(response, error, null);
		});
	},

	setCompetitionResults: (params, response) => {
		let currentCompetition = JSON.parse(params.competition);
		//console.log('setCompetitionResults currentCompetition ', currentCompetition);

		updateCompetitionIteration(currentCompetition, (competition) => {
			//console.log('setCompetitionResults competition ', competition);
			let participantsResults = competition.currentParticipants;

			updateCompetitionResults(participantsResults, competition.id, (success, competedParticipants) => {
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

	initCompetitionForIterations: (params, response) => {

		getCompetitionById(params.competitionId, (success, result) => {
			if(success) {
				let competition = result;
				let newParticipants = filters.filterCompetedParticipants(competition.participants);

				if(!Object.keys(newParticipants).length && Object.keys(competition.participants).length) {
					getCompetitionResults(competition, (success, result) => {
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

let getCollectionByFilter = (collectionName, filter, value, callback) => {
	let db = admin.database();
	let collectionRef = db.ref(collectionName);

	collectionRef.orderByChild(filter).equalTo(value).on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}

let getCollectionByName = (collectionName, callback) => {
	let db = admin.database();
	let collectionRef = db.ref(collectionName);

	collectionRef.on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}

let joinToCompetition = (params, callback) => {
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

	competitionsRef.on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
};

let updateCompetitionResults = (participantsResults, competitionId, callback) => {
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
	presonalResultsRef.on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}

let getCompetitionResults = (competition, callback) => {
	let db = admin.database();
	let presonalResultsRef = db.ref('personalResults/' + competition.id + '/');

	presonalResultsRef.on('value', (snapshot) => {
		if(!snapshot.val()) {
			createCompetitionResults(competition, (success) => {
				callback(success, snapshot.val());
			});	
		}
		else {
			callback(true, snapshot.val());
		}
	}, (error) => {
		callback(false, error);
	});
}

let getNewParticipants = (competition, sortedParticipants) => {
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


let getNewParticipantsFromGendger = (participants, numOfParticipants) => {
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

let getCompetitionById = (competitionId, callback) => {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competitionId);

	competitionsRef.on('value', (snapshot) => { 
		callback(true, snapshot.val());

		
		/*let competition;
		competitionsRef = db.ref('competitions/-LAXsGT3ZyzpPsfjcmve');
		competitionsRef.on('value', (snapshot2) => { 
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


	}, (error) => {
		callback(false, error);
	});
}

let attachIdToObject = (snapshot) => {
	let resObj = snapshot.val();
	let resObjId = Object.assign({}, resObj, { 'id': snapshot.key }); //add the id to the object
	return resObjId;
}

let sortPersonalResults = (currentCompetition, results) => {
	let today = moment(new Date());
	//map results by age
	let resultsMap = filters.sortParticipantsByAge(results);

	//order results by gender
	Object.keys(resultsMap).forEach((resultsByAge) => {
		let currentAgeResults = resultsMap[resultsByAge];

		arraySortByScore(currentAgeResults.males);
		arraySortByScore(currentAgeResults.females);
	});
	return resultsMap;
}

let arraySortByScore = (arrayList) => {
	let today = moment(new Date());
	arrayList.sort((itemA, itemB) => {
	    if (parseFloat(itemA.score) < parseFloat(itemB.score)) {
	        return -1;
	    }
	    else if (parseFloat(itemA.score) > parseFloat(itemB.score)) {
	        return 1;
	    }
	    return 0;
	});
}

let updateCompetitionIteration = (competition, callback) => {
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

	competitionsRef.on('value', (snapshot) => {
		callback(snapshot.val());
	}, (error) => {
		callback(null);
	});
}

let updateCompetition = (competition) => {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competition.id + '/');
	competitionsRef.update(competition);
}

let createCompetitionResults = (competition, callback) => {
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
	presonalResultsRef.on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}