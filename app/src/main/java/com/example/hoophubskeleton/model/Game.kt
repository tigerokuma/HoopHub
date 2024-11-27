package com.example.hoophubskeleton.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

// This represents a game stored in Firebase. These game objects are created when one
// player sends another player an invite to a game.


data class Game(
    var id: String = "",
    var gameDateTime: Timestamp = Timestamp.now(), // Firestore Timestamp
    var location: GeoPoint = GeoPoint(0.0, 0.0), // Use GeoPoint instead of List<Double>
    var skillLevel: String = "",
    var participants: List<String> = mutableListOf()
)
