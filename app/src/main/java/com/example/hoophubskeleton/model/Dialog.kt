package com.example.hoophubskeleton.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth


data class Dialog(
    var dialogId: String = "", // Document ID in Firestore
    val participants: List<String> = emptyList(), // User IDs in the conversation
    val latestMessage: String = "", // Preview of the latest message
    val latestTimestamp: Timestamp? = null // Timestamp of the latest message
)