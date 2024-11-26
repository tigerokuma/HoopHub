package com.example.hoophubskeleton.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

// This represents a game stored in Firebase. These game objects are created when one
// player sends another player an invite to a game.


enum class GameStatus {
    PENDING,
    ACCEPTED,
    CANCELLED,
    DECLINED
}


data class Game(
    val id: String = "",
    val createdBy: String = "",
    val sentTo: String = "",
    val gameDateTime: Timestamp = Timestamp.now(),
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    var status: GameStatus = GameStatus.PENDING,
    var timestamp: Timestamp = Timestamp.now()
)
