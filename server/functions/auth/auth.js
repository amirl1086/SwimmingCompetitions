
var functions = require('firebase-functions');
var admin = require('firebase-admin');

module.exports = {
    addNewUser: function(params) {
    	console.log('addNewUser params ', params);

    	functions.https.onRequest(function(request, response) {

			var newUser = {
				firstName: params.firstName,
				lastName: params.lastName,
				email: params.email,
				password: params.password
			}

			firbase.createUserWithEmailAndPassword(newUser.email, newUser.password, function(firebaseUser) {
				console.log('firebaseUser ', firebaseUser);
				console.log('firebaseUserId ', firebaseUser.uid);
				response.send('OK');
			}).catch(function(error) {
				
			});
		  	
		});
	},

	logIn: function(params) {
		console.log('logIn params ', params);

		var logInUser = {
			email: params.email,
			password: params.password
		}

		firebase.auth().signInWithEmailAndPassword(logInUser.email, logInUser.password, function(firebaseUser){
			console.log('firebaseUser ', firebaseUser);
			console.log('firebaseUserId ', firebaseUser.uid);
			response.send(firebaseUser);
		}).catch(function(error) {
		  	// Handle Errors here.
		  	//var errorCode = error.code;
		  	//var errorMessage = error.message;
		  	// ...
		  	response.send(null);
		});
	}
	
};

