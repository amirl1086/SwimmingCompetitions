
const functions = require('firebase-functions');
const admin = require('firebase-admin');

exports.addNewUser = functions.https.onRequest(function(request, response) {
	var params = request.params;
	var newUser = {
		firstName: params.firstName,
		lastName: params.lastName,
		email: params.email
		password: params.password
	}

	firbase.createUserWithEmailAndPassword(newUser.email, newUser.password, function(firebaseUser) {
		console.log('firebaseUser ', firebaseUser);
		console.log('firebaseUserId ', firebaseUser.uid);

	}).catch(function(error) {
		
	});
	
  	firebase.database().ref('users/' + userId).set(newUser);
  	response.send('OK');
});