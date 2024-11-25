package com.example.hoophubskeleton.repository

import com.example.hoophubskeleton.model.Game
import com.google.firebase.firestore.FirebaseFirestore

class GamesRepository(private val firestore: FirebaseFirestore) {

    // Return a list of all games.
    fun getAllGames(callback: (List<Game>?, String?) -> Unit) {
        firestore.collection("games").get()
            .addOnSuccessListener { result ->
                val games = result.toObjects(Game::class.java)
                callback(games, null)
            }
            .addOnFailureListener{ e ->
                callback(null, e.message)
            }
    }

    // Return a list of games initiated by a player
    fun getGamesStartedByUser(userId : String, callback: (List<Game>?, String?) -> Unit) {
        firestore.collection("games")
            .whereEqualTo("createdBy", userId)
            .get()
            .addOnSuccessListener { result ->
                val games = result.toObjects(Game::class.java)
                callback(games, null)
            }
            .addOnFailureListener { e ->
                callback(null, e.localizedMessage ?: "Unknown error")
            }
    }

    // Return a list of games a player was invited to
    fun getGamesUserInvitedTo(userId: String, callback: (List<Game>?, String?) -> Unit) {
        firestore.collection("games")
            .whereEqualTo("sentTo", userId)
            .get()
            .addOnSuccessListener { result ->
                val games = result.toObjects(Game::class.java)
                callback(games, null)
            }
            .addOnFailureListener { e ->
                callback(null, e.localizedMessage ?: "Unknown error")
            }
    }

    // Return a list of all games that a player is involved in
    fun getAllGamesForUser(userId: String, callback: (List<Game>?, String?) -> Unit) {
        getGamesStartedByUser(userId) { createdGames, createdError ->
            if (createdGames != null) {
                getGamesUserInvitedTo(userId) { invitedGames, invitedError ->
                    if(invitedGames != null) {
                        val allGames = createdGames + invitedGames
                        callback(allGames, null)
                    } else {
                        callback(null, invitedError)
                    }
                }
            } else {
                callback(null, createdError)
            }
        }
    }
}