package com.example.hoophubskeleton.model

// This will allow us to handle the three different phases a game could be in
enum class CardType { PENDING_SENT, PENDING_RECEIVED, ACCEPTED }

data class BookingCard(
    val otherPlayerName: String,
    val otherPlayerImageUrl: String?,
    val competitionLevel: String,
    val location: String,
    val dateTime: String, // probably need to change date and location later
    val cardType: CardType
)