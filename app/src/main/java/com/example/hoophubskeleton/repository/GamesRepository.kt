package com.example.hoophubskeleton.repository

import android.location.Location
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.model.GameStatus


import com.google.firebase.Timestamp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FieldValue


class GamesRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getGamesNearLocation(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        callback: (List<Game>) -> Unit
    ) {
        firestore.collection("games")
            .get()
            .addOnSuccessListener { snapshot ->
                val games = snapshot.toObjects(Game::class.java)
                val nearbyGames = games.filter { game ->
                    val gameLocation = game.location
                    val userLocation = Location("").apply {
                        setLatitude(latitude)
                        setLongitude(longitude)
                    }
                    val gameLocationLatLng = Location("").apply {
                        setLatitude(gameLocation.latitude)
                        setLongitude(gameLocation.longitude)
                    }
                    gameLocationLatLng.distanceTo(userLocation) <= (radiusKm * 1000)
                }
                callback(nearbyGames)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                callback(emptyList()) // Return an empty list in case of error
            }
    }


    fun getCurrentUserLocation(callback: (Double, Double) -> Unit) {
        // Simulate fetching user's current location
        // Replace this with actual location data
        callback(37.7749, -122.4194) // San Francisco, for example
    }

    fun addUserToGameParticipants(gameId: String, userId: String, onComplete: () -> Unit) {
        val gameRef = firestore.collection("games").document(gameId)
        gameRef.update("participants", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                onComplete() // Notify ViewModel that the operation succeeded
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                //  Handle error scenarios
            }
    }


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
        firestore.collection("games")
            // Find the arrays that user is in
            .whereArrayContains("participants", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // firebase documents to objects
                val games = querySnapshot.toObjects(Game::class.java)
                // return list of games
                callback(games, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception.message)
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

    fun leaveGame(gameId: String, userId: String, callback: (Boolean) -> Unit) {
        firestore.collection("games").document(gameId)
            .update("participants", FieldValue.arrayRemove(userId))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun listenToGamesForUser(userId: String, callback: (List<Game>?, String?) -> Unit) {
        firestore.collection("games")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    callback(null, exception.message)
                    return@addSnapshotListener
                }
                val games = querySnapshot?.toObjects(Game::class.java)
                callback(games, null)
            }
    }

}

