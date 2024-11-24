package com.example.hoophubskeleton.repository

import com.example.hoophubskeleton.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val firestore: FirebaseFirestore) {

    suspend fun getUserById(uid: String): User? {
        return try {
            val userSnapshot = firestore.collection("users").document(uid).get().await()
            if (userSnapshot.exists()) {
                userSnapshot.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createOrFetchDialog(currentUserId: String, otherUserId: String, message: String) {
        val dialogsRef = firestore.collection("dialogs")
        val dialogId = if (currentUserId < otherUserId) {
            "$currentUserId-$otherUserId"
        } else {
            "$otherUserId-$currentUserId"
        }

        val dialogDoc = dialogsRef.document(dialogId)
        val dialogSnapshot = dialogDoc.get().await()

        if (!dialogSnapshot.exists()) {
            dialogDoc.set(
                mapOf(
                    "users" to listOf(currentUserId, otherUserId),
                    "lastUpdated" to System.currentTimeMillis()
                )
            )
        }

        dialogDoc.collection("messages").add(
            mapOf(
                "senderId" to currentUserId,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }
}
