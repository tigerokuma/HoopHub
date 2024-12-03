package com.example.hoophubskeleton.repository

import android.location.Location
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.model.GameStatus


import com.google.firebase.Timestamp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FieldValue
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.app.Activity




class GamesRepository {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

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


    fun getCurrentUserLocation(activity: Activity, callback: (Double, Double) -> Unit) {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check if location services are enabled
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isLocationEnabled) {
            callback(37.7749, -122.4194) // Fallback: San Francisco
            return
        }

        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Permissions granted, fetch location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback(location.latitude, location.longitude)
            } else {
                callback(37.7749, -122.4194) // Fallback: San Francisco
            }
        }.addOnFailureListener {
            callback(37.7749, -122.4194) // Fallback: San Francisco
        }
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

