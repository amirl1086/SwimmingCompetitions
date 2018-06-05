
const admin = require('firebase-admin');
const utilities = require('./utils.js');
const filters = require('./filters.js');
const authentication = require('../auth/auth.js');
const moment = require('moment');

module.exports = {

	getCompetitionInProgress: (response) => {
		getCollectionByFilter('competitions', 'inProgress', 'true', (success, result) => {
			if(success) {
				if(result) {
					utilities.sendResponse(response, null, result);
				}
				else {
					utilities.sendResponse(response, 'no_live_competition', null);
				}
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
				utilities.sendResponse(response, 'no_such_email', null);
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



	setNewCompetition: (competitionParams, response) => {
		let db = admin.database();

		let newCompetitionKey;
		if(competitionParams.id) {
			newCompetitionKey = competitionParams.id;
		}
		else {
			newCompetitionKey = db.ref('competitions').push().key;
		}
		
		let newCompetition = {
			'name': competitionParams.name,
			'activityDate': competitionParams.activityDate,
			'swimmingStyle': competitionParams.swimmingStyle,
			'numOfParticipants': competitionParams.numOfParticipants,
			'length': competitionParams.length,
			'toAge': competitionParams.toAge,
			'fromAge': competitionParams.fromAge,
			"id": newCompetitionKey
		};

		let newCompetitionRef = db.ref('competitions/' + newCompetitionKey);
		//console.log('competitionParams ', competitionParams);

		newCompetitionRef.update(newCompetition);

		newCompetitionRef.on('value', (snapshot) => {
			utilities.sendResponse(response, null, snapshot.val());
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
				console.log('getParticipantStatistics selectedCompetitions ', selectedCompetitions);

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

	getPersonalResultsByCompetitionId: (params, response) => {
		let competition = JSON.parse(params.competition);
		let competitionId = competition.id;

		getCompetitionResults(competition, (success, result) => {
			if(success) {
				let resultsAgeMap = filters.sortPersonalResults(competition, result);
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
		console.log('setCompetitionResults currentCompetition ', currentCompetition);

		updateCompetitionPerIteration(currentCompetition, (success, result) => {
			if(success) {
				let competition = result;
				console.log('setCompetitionResults competition ', competition);
				let participantsResults = currentCompetition.currentParticipants;

				updateCompetitionResults(participantsResults, competition.id, (success, competedParticipants) => {
					if(success) {
						console.log('setCompetitionResults competition.participants ', competition.participants);
						console.log('setCompetitionResults competedParticipants ', competedParticipants);
						let newParticipants = filters.filterCompetedParticipants(competition.participants);
						console.log('setCompetitionResults newParticipants ', newParticipants);

						if(newParticipants && Object.keys(newParticipants).length === 0) {
							//set flags and update the competition
							competition.isDone = 'true';
							competition.inProgress = 'false';
							delete competition.currentParticipants;
							updateCompetition(competition);
							
							//query all results
							let db = admin.database();
							let personalResultsRef = db.ref('personalResults/' + competition.id);
						
							personalResultsRef.on('value', (snapshot) => {
								let personalResults = snapshot.val();
								let resultsAgeMap = filters.sortPersonalResults(competition, personalResults);
								resultsAgeMap.type = 'resultsMap';
								utilities.sendResponse(response, null, resultsAgeMap);
							}, 
							(error) => {
								utilities.sendResponse(response, error, null);
							});
						}
						else {
							//competition.participants = participants;
							let sortedParticipants = filters.sortParticipantsByAge(newParticipants);
							//console.log('setCompetitionResults sortedParticipants ', sortedParticipants);
							filters.removeBlankSpots(competition, sortedParticipants);
							//console.log('setCompetitionResults removeBlankSpots sortedParticipants ', sortedParticipants);
							competition.currentParticipants = filters.getNewParticipants(competition, sortedParticipants);
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
			}
			else {
				utilities.sendResponse(response, result, null);
			}
		});
	},

	initCompetitionForIterations: (params, response) => {

		getObjectById('competitions', params.competitionId, (success, result) => {
			if(success) {
				let competition = result;
				let newParticipants = filters.filterCompetedParticipants(competition.participants);

				console.log('newParticipants ', newParticipants);

				if(!Object.keys(newParticipants).length && Object.keys(competition.participants).length) {
					getCompetitionResults(competition, (success, result) => {
						if(success) {
							let resultsAgeMap = filters.sortPersonalResults(competition, result);
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
					//console.log('initCompetitionForIterations sortedParticipants ', sortedParticipants);
					filters.removeBlankSpots(competition, sortedParticipants);
					//console.log('initCompetitionForIterations removeBlankSpots ', sortedParticipants);
					competition.currentParticipants = filters.getNewParticipants(competition, sortedParticipants);
					//console.log('competition.currentParticipants ', competition.currentParticipants);

					//mark compeition started for real-time view
					competition.inProgress = 'true';
					updateCompetition(competition);

					competition.type = 'newIteration';
					utilities.sendResponse(response, null, competition);
				}
			}
			else {
				utilities.sendResponse(response, result, null);
			}

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


	console.log('participantsResults ', participantsResults);

	let personalResults = {};
	for(let key in participantsResults) {
		console.log('key ', key);
		let currentParticipant = participantsResults[key];
		personalResults[currentParticipant.uid] = {
			'firstName': currentParticipant.firstName,
			'lastName': currentParticipant.lastName,
			'birthDate': currentParticipant.birthDate,
			'gender': currentParticipant.gender,
			'score': currentParticipant.score,
			'uid': currentParticipant.uid,
			'timeStamp': moment().format('DD/MM/YYYY HH:mm:ss')
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
			callback(false, null);
		}
		else {
			callback(true, snapshot.val());
		}
	}, (error) => {
		callback(false, error);
	});
}

let getObjectById = (collectionName, keyValue, callback) => {
	let db = admin.database();
	let competitionsRef = db.ref(collectionName + '/' + keyValue);

	competitionsRef.on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}

let updateCompetitionPerIteration = (competition, callback) => {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competition.id + '/');

	competition.participants = JSON.parse(competition.participants);
	competition.currentParticipants = JSON.parse(competition.currentParticipants);

	console.log('competition.participants ', competition.participants);
	console.log('competition.currentParticipants ', competition.currentParticipants);


	for(let key in competition.currentParticipants) {
		if(competition.participants[key]) {
			competition.participants[key].competed = 'true';
		}
	}

	competitionsRef.update(competition);

	competitionsRef.on('value', (snapshot) => {
		callback(true, snapshot.val());
	}, (error) => {
		callback(false, error);
	});
}

let updateCompetition = (competition) => {
	let db = admin.database();
	let competitionsRef = db.ref('competitions/' + competition.id + '/');
	competitionsRef.update(competition);
}