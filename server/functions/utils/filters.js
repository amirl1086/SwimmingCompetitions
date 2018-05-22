
const moment = require('moment');

module.exports = {

	filterCompetedParticipants: function(participants) {
		let newParticipants = {};
		for(let key in participants) {
			if(!participants[key].competed || participants[key].competed === 'false') {
				newParticipants[key] = participants[key];
			}
		}
		return newParticipants;
	},

	sortParticipantsByAge: function(participants) {
		let today = moment(new Date());
		//map results by age
		return Object.keys(participants).reduce(function(totalResults, key) {
			let participant = participants[key];
			let compare = moment(participant.birthDate, 'DD/MM/YYYY hh:mm');
			let participantAge = Math.floor(today.diff(compare, 'years', true));

			if(!totalResults[participantAge]) {
				totalResults[participantAge] = { 'males': [], 'females': [] };
			}
			participant.gender === 'male' ? totalResults[participantAge].males.push(participant) : totalResults[participantAge].females.push(participant);
			return totalResults;
		}, {});
	},

	removeBlankSpots: function(competition, sortedParticipants) {
		Object.keys(sortedParticipants).forEach(function(ageKey) {
			let currentAgeParticipants = sortedParticipants[ageKey];

			//loop over the gender
			Object.keys(currentAgeParticipants).forEach(function(genderKey) {
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

	filterCompetitions : function(competitions, params) {
		let currentUser = params.currentUser;
		let today = moment();
		let birthDate = moment(currentUser.birthDate, 'DD/MM/YYYY hh:mm');
		let userAge = Math.floor(today.diff(birthDate, 'years', true));
		let filters = params.filters.split(',');

		let filteredCompetitions = {};

		for(let key in competitions) {
			let currentCompetition = competitions[key];

			for(let i = 0; i < filters.length; i++) {
				let filterName = filters[i];
				if(filterName === 'uid') {
					if(searchInParticipants(currentCompetition, currentUser.uid)) {
						filteredCompetitions[key] = competitions[key];
					}
				}
				else if(filterName === 'age') {
					let fromAge = parseInt(currentCompetition.fromAge);
					let toAge = parseInt(currentCompetition.toAge);
					if(userAge >= fromAge && userAge <= toAge) {
						filteredCompetitions[key] = competitions[key];
					}
				}
				else if(filterName === 'isDone') {
					if(currentCompetition.isDone) {
						filteredCompetitions[key] = competitions[key];
					}
				}
			}
		}

		return filteredCompetitions;
	}

}