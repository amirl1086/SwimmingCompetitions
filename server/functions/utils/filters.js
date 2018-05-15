
var moment = require('moment');

module.exports = {

	filterCompetedParticipants: function(participants) {
		var newParticipants = {};
		for(var key in participants) {
			if(!participants[key].competed || participants[key].competed === 'false') {
				newParticipants[key] = participants[key];
			}
		}
		return newParticipants;
	},

	sortParticipantsByAge: function(participants) {
		var today = moment(new Date());
		//map results by age

		//console.log('sortParticipantsByAge participants ', JSON.stringify(participants));	
		return Object.keys(participants).reduce(function(totalResults, key) {
			var participant = participants[key];

			//console.log('sortParticipantsByAge participant ', participant);

			var compare = moment(participant.birthDate, 'DD/MM/YYYY hh:mm');
			//console.log('compare ', compare.format('DD/MM/YYYY hh:mm'))

			var participantAge = Math.floor(today.diff(compare, 'years', true));
			//console.log('participantAge ', participantAge);

			if(!totalResults[participantAge]) {
				totalResults[participantAge] = { 'males': [], 'females': [] };
			}
			participant.gender === 'male' ? totalResults[participantAge].males.push(participant) : totalResults[participantAge].females.push(participant);
			return totalResults;
		}, {});
	},

	removeBlankSpots: function(competition, sortedParticipants) {
		Object.keys(sortedParticipants).forEach(function(ageKey) {
			var currentAgeParticipants = sortedParticipants[ageKey];

			//console.log('currentAgeParticipants ' + JSON.stringify(currentAgeParticipants));

			//loop over the gender
			Object.keys(currentAgeParticipants).forEach(function(genderKey) {
				var currentGenderParticipants = currentAgeParticipants[genderKey];

				console.log('currentGenderParticipants ' + JSON.stringify(currentGenderParticipants));
				console.log('genderKey ' + JSON.stringify(genderKey));

				//check if there are missing participants for the current age and gender
				var leftovers = currentGenderParticipants.length % parseInt(competition.numOfParticipants);

				console.log('leftovers ' + JSON.stringify(leftovers));

				if(leftovers != 0) {
					if(!sortedParticipants['0']) {
						sortedParticipants['0'] = { 'males': [], 'females': [] };
					}
					for(var i = currentGenderParticipants.length - leftovers; i < currentGenderParticipants.length; i++) {
						//add it to the missing map
						sortedParticipants['0'][genderKey].push(Object.assign({}, currentGenderParticipants[i]));
						//remove it from the participants map
						currentGenderParticipants.splice(i--, 1);
					}
				}
			});
		});

		//console.log('participantsByAges ' + JSON.stringify(sortedParticipants));
	},

	filterCompetitions : function(competitions, params) {
		var currentUser = JSON.parse(params.currentUser);
		var today = moment();
		var birthDate = moment(currentUser.birthDate, 'DD/MM/YYYY hh:mm');
		var userAge = Math.floor(today.diff(birthDate, 'years', true));
		var filters = params.filters.split(',');

		var filteredCompetitions = {};

		//console.log('competitions ',  competitions);
		//console.log('params ',  params);

		for(var key in competitions) {
			var currentCompetition = competitions[key];
			//console.log('currentCompetition ',  currentCompetition);

			for(var i = 0; i < filters.length; i++) {
				switch(filters[i]) {
					case 'uid': {
						if(searchInParticipants(currentCompetition, currentUser.uid)) {
							filteredCompetitions[key] = competitions[key];
						}
						break;
					}
					case 'age': {
						var fromAge = parseInt(currentCompetition.fromAge);
						var toAge = parseInt(currentCompetition.toAge);
						if(userAge >= fromAge && userAge <= toAge) {
							filteredCompetitions[key] = competitions[key];
						}
						break;
					}
					case 'isDone': {
						if(currentCompetition.isDone) {
							filteredCompetitions[key] = competitions[key];
						}
						break;
					}
				}
			}
		}

		return filteredCompetitions;
	}

}