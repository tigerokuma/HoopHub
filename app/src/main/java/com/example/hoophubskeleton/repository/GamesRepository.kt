package com.example.hoophubskeleton.repository

import android.location.Location
import com.example.hoophubskeleton.model.Game

import com.google.firebase.firestore.FieldValue

import com.example.hoophubskeleton.model.GameStatus
import com.google.firebase.Timestamp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

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

