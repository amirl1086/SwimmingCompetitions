
module.exports = {

	sendResponse: (response, error, resultObj) => {
		if(error) {
			response.send({ 'success': false, 'data': error });
		}
		else {
			response.send({ 'success': true, 'data': resultObj });
		}
	},

	mapToList: (map) => {
		let result = [];
		for(let key in map) {
			result.push(map[key]);
		}
		return result;
	},

	listToMap: (list, key) => {
		let result = {};
		for(let i in list) {
			result[list[i][key]] = list[i];
		}
		return result;
	}
};




// //db manipulation
// exports.resetCompetitions = functions.https.onRequest((request, response) => {
// 	let db = admin.database();
// 	let collectionRef = db.ref('competitions');

// 	collectionRef.on('value', (snapshot) => {
// 		let competitions = snapshot.val();
// 		for (let compId in competitions) {
// 			for (let userId in competitions[compId].participants) {
// 				competitions[compId].participants[userId].competed = 'false';
// 			}
// 			competitions[compId].isDone = 'false';
// 			competitions[compId].inProgress = 'false';
// 			delete competitions[compId].currentParticipants;
// 		}
// 		collectionRef.update(competitions);

// 		collectionRef = db.ref('personalResults');
// 		collectionRef.remove();

// 		response.send('success\n');
// 	}, (error) => {
// 		response.send('error ', error, '\n');
// 	});
// });