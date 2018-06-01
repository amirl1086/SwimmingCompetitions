
const moment = require('moment');

module.exports = {

	filterCompetedParticipants: (participants) => {
		let newParticipants = {};
		for(let key in participants) {
			if(!participants[key].competed || participants[key].competed === 'false') {
				newParticipants[key] = participants[key];
			}
		}
		return newParticipants;
	},

	sortParticipantsByAge: (participants) => {
		return sortParticipantsByAge(participants);
	},

	removeBlankSpots: (competition, sortedParticipants) => {
		console.log('removeBlankSpots sortedParticipants ', sortedParticipants);

		Object.keys(sortedParticipants).forEach((ageKey) => {
			let currentAgeParticipants = sortedParticipants[ageKey];

			//loop over the gender
			Object.keys(currentAgeParticipants).forEach((genderKey) => {
				let currentGenderParticipants = currentAgeParticipants[genderKey];

				console.log('currentGenderParticipants ' + JSON.stringify(currentGenderParticipants));
				console.log('genderKey ' + JSON.stringify(genderKey));

				//check if there are missing participants for the current age and gender
				let leftovers = currentGenderParticipants.length % parseInt(competition.numOfParticipants);

				console.log('leftovers ' + JSON.stringify(leftovers));

				if(leftovers != 0) {
					if(!sortedParticipants['0']) {
						sortedParticipants['0'] = { 'males': [], 'females': [] };
					}
					for(let i = currentGenderParticipants.length - leftovers; i < currentGenderParticipants.length; i++) {
						//add it to the missing map
						sortedParticipants['0'][genderKey].push(Object.assign({}, currentGenderParticipants[i]));
						//remove it from the participants map
						currentGenderParticipants.splice(i--, 1);
					}
				}
			});
		});
	},

	getNewParticipants: (competition, sortedParticipants) => {
		let newParticipants = {};
		let currentAge = parseInt(competition.toAge);
		let fromAge = parseInt(competition.fromAge);
	
		while(currentAge >= 0) {
			if(sortedParticipants[currentAge.toString()]) {
				if(sortedParticipants[currentAge.toString()].males.length) {
					newParticipants = getNewParticipantsFromGendger(sortedParticipants[currentAge.toString()].males, parseInt(competition.numOfParticipants));
				}
				else if(sortedParticipants[currentAge.toString()].females.length) {
					newParticipants = getNewParticipantsFromGendger(sortedParticipants[currentAge.toString()].females, parseInt(competition.numOfParticipants));
				}
				if(Object.keys(newParticipants).length) {
					break;
				}
			}
			currentAge--;
		}
		return newParticipants;
	},

	filterCompetitions : (competitions, params) => {
		let currentUser = params.currentUser;
		let today = moment();
		let userAge;

		if(currentUser) {
			let birthDate = moment(currentUser.birthDate, 'DD/MM/YYYY hh:mm');
			userAge = Math.floor(today.diff(birthDate, 'years', true));
		}

		let filters = params.filters.split(',');
		let filteredCompetitions = {};

		for(let key in competitions) {
			let currentCompetition = competitions[key];

			for(let i = 0; i < filters.length; i++) {
				let filterName = filters[i];
				if(filterName === 'age') {
					let fromAge = parseInt(currentCompetition.fromAge);
					let toAge = parseInt(currentCompetition.toAge);
					if(userAge && userAge >= fromAge && userAge <= toAge) {
						filteredCompetitions[key] = competitions[key];
					}
				}
				else if((filterName === 'swimmingStyle' && competitions[key].swimmingStyle === params.swimmingStyle) || 
						(currentUser && filterName === 'uid' && searchInParticipants(currentCompetition, currentUser.uid)) ||
						(filterName === 'isDone' && currentCompetition.isDone)) {

					filteredCompetitions[key] = competitions[key];
				}
			}
		}

		return filteredCompetitions;
	},

	sortPersonalResults: (currentCompetition, results) => {
		let today = moment(new Date());
		//map results by age
		let resultsMap = sortParticipantsByAge(results);
	
		//order results by gender
		Object.keys(resultsMap).forEach((resultsByAge) => {
			let currentAgeResults = resultsMap[resultsByAge];
	
			arraySortByScore(currentAgeResults.males);
			arraySortByScore(currentAgeResults.females);
		});
		return resultsMap;
	}

};

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

let searchInParticipants = (competition, uid) => {
	if(competition.participants) {
		let participantKey = Object.keys(competition.participants).find((participant, key) => key === uid);
		return participantKey != null;
	}
	return false;
}

let sortParticipantsByAge = (participants) => {
	let today = moment(new Date());
	//map results by age
	return Object.keys(participants).reduce((totalResults, key) => {
		let participant = participants[key];
		let compare = moment(participant.birthDate, 'DD/MM/YYYY hh:mm');
		let participantAge = Math.floor(today.diff(compare, 'years', true));

		if(!totalResults[participantAge]) {
			totalResults[participantAge] = { 'males': [], 'females': [] };
		}
		participant.gender === 'male' ? totalResults[participantAge].males.push(participant) : totalResults[participantAge].females.push(participant);
		return totalResults;
	}, {});
}

let getNewParticipantsFromGendger = (participants, numOfParticipants) => {
	let newParticipants = {};
	let totalSelected = 0;
	for(let i = 0; i < participants.length; i++) {
		if((!participants[i].competed || participants[i].competed === 'false') && totalSelected < numOfParticipants) {
			newParticipants[participants[i].uid] = participants[i];
			totalSelected++
		}
	}
	return newParticipants;
}