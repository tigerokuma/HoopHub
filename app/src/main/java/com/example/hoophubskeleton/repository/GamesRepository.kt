package com.example.hoophubskeleton.repository

import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.model.GameStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime

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

    // Create an invite
    fun createInvite(game: Game, callback: (Boolean) -> Unit) {
        // Generate a new document ID for the game
        val newGameRef = firestore.collection("games").document()

        // Set the document ID as the game ID
        val gameWithId = game.copy(id = newGameRef.id)

        newGameRef.set(gameWithId)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }

    fun acceptInvite(gameId: String, callback: (Boolean) -> Unit) {
        firestore.collection("games").document(gameId)
            .update("status", GameStatus.ACCEPTED)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun declineInvite(gameId: String, callback: (Boolean) -> Unit) {
        firestore.collection("games").document(gameId)
            .update("status", GameStatus.DECLINED)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener {callback(false)}
    }

    fun cancelInvite(gameId: String, callback: (Boolean) -> Unit) {
        firestore.collection("games").document(gameId)
            .update("status", GameStatus.CANCELLED)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun cancelGame(gameId: String, callback: (Boolean) -> Unit) {
       firestore.collection("games").document(gameId)
           .update("status", GameStatus.CANCELLED.name)
           .addOnSuccessListener { callback(true) }
           .addOnFailureListener { callback(false) }
    }

}