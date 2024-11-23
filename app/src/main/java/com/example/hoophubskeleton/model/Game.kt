package com.example.hoophubskeleton.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

// This represents a game stored in Firebase. These game objects are created when one
// player sends another player an invite to a game.

class Game(
    var createdBy: String = "",
    var sentTo: String = "",
    var gameDateTime: Timestamp,
    var inviteAccepted: Boolean= false,
    var location: GeoPoint = GeoPoint(0.0, 0.0), // latLng?
    var timestamp: Timestamp = Timestamp.now() // when invite created
) {
}