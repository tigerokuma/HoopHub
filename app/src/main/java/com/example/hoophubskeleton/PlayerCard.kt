package com.example.hoophubskeleton

// This class represents a player card. The PlayersFragment will display a list of
// player cards.
data class PlayerCard(
    var name: String,
    var rating: Double,
    var location: String, // maybe change later
    var imageId: Int,
    var competitionLevel: String
)