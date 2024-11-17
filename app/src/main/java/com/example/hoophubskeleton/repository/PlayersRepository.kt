package com.example.hoophubskeleton.repository
import com.example.hoophubskeleton.model.User
import com.google.firebase.firestore.FirebaseFirestore

class PlayersRepository(private val firestore: FirebaseFirestore) {

    // Return a list of all players.
    // Use a callback function because we're fetching data asynchronously ("get()").
    fun getPlayers(callback: (List<User>?, String?) -> Unit) {
        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                val players = result.toObjects(User::class.java)
                callback(players, null)
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }
}