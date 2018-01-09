
module.exports = {

	sendResponse: function(response, error, resultObj) {

		if(error) {
			response.status(500).send({ 'success': false, 'data': error });
		}
		else {
			response.status(200).send({ 'success': true, 'data': resultObj });
		}
	}
}