import firebase from 'firebase/app';
import 'firebase/database';
import 'firebase/auth';

var config = {
    apiKey: "AIzaSyDNjZU_7jyczIvvsMtb9fmPC92Ya1_L-Ps",
    databaseURL: "https://logbook-1587535702012.firebaseio.com",
    // authDomain: "logbook-1587535702012.firebaseapp.com"
    authDomain: "saillogs.web.app"
};
firebase.initializeApp(config);

const dbref = firebase.database().ref();
const trips = dbref.child("trips");

export const userTrips = function(userId) {
    return trips.child(userId);
}

export const singleTrip = function(userId, tripId) {
    return userTrips(userId).child(tripId);
}

export const googleAuthProvider = new firebase.auth.GoogleAuthProvider();

export const firebaseAuth = firebase.auth();