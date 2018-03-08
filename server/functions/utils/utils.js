
module.exports = {

	sendResponse: function(response, error, resultObj) {

		if(error) {
			response.send({ 'success': false, 'data': error });
		}
		else {
			response.send({ 'success': true, 'data': resultObj });
		}
	}
}