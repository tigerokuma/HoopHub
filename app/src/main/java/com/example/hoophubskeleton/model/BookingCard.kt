package com.example.hoophubskeleton.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


// This will allow us to handle the three different phases a game could be in
//enum class CardType { PENDING_SENT, PENDING_RECEIVED, ACCEPTED }

data class BookingCard(
    val gameId: String,
    val participantNames: List<String>, // Names of participants
    val participantImages: List<String?>, // Profile images of participants
    val competitionLevel: String, // General competition level (e.g., "Casual")
    val location: GeoPoint,
    val dateTime: Timestamp,
    val maxParticipants: Int,
    val courtName: String
)
