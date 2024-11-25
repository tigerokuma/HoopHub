package com.example.hoophubskeleton.model

// This class represents a player card. The PlayersFragment will display a list of
// player cards.
data class PlayerCard(
    var uid: String,
    var name: String,
    var rating: Double,
    var location: String, // maybe change later
    var imageId: String?,
    var competitionLevel: String
)