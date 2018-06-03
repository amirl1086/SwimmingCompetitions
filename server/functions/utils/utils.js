
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