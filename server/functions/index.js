


const firebase = require('firebase-admin');

const serviceAccount = require("./firebase-swimmingcompetitions-firebase-adminsdk-by0h1-3444f4cabe.json");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://fir-swimmingcompetitions.firebaseio.com"
});


var auth = require('./auth/auth.js');



