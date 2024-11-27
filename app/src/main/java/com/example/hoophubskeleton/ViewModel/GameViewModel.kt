package com.example.hoophubskeleton.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.repository.GamesRepository
import com.google.firebase.Timestamp

import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.map
import com.example.hoophubskeleton.model.BookingCard
//import com.example.hoophubskeleton.model.GameStatus
import com.google.firebase.auth.FirebaseAuth


class GameViewModel : ViewModel() {

    private val repository = GamesRepository()

    // LiveData to hold the list of games
    private val _upcomingGames = MutableLiveData<List<Game>>()
    val upcomingGames: LiveData<List<Game>> get() = _upcomingGames

    // Fetch games near a specific location
    fun fetchGamesNearLocation(latitude: Double, longitude: Double, radiusKm: Double = 100.0) {
        repository.getGamesNearLocation(latitude, longitude, radiusKm) { games ->
            // Filter games to show only upcoming ones
            val currentTime = Timestamp.now()
            val filteredGames = games.filter { it.gameDateTime > currentTime } // Only future games
            _upcomingGames.postValue(filteredGames) // Update LiveData
        }
    }


    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error


//    fun fetchAllGames() {
//        repository.getAllGames { gameList, errorMsg ->
//            handleGamesCallback(gameList, errorMsg)
//        }
//    }
//
//    fun fetchGamesStartedByUser(userId: String) {
//        repository.getGamesStartedByUser(userId) { gameList, errorMsg ->
//            handleGamesCallback(gameList, errorMsg)
//        }
//    }

//    fun fetchGamesUserInvitedTo(userId: String) {
//        repository.getGamesUserInvitedTo(userId) { gameList, errorMsg ->
//            handleGamesCallback(gameList, errorMsg)
//        }
//    }
//
//    fun fetchAllGamesForUser(userId: String) {
//        repository.getAllGamesForUser(userId) { gameList, errorMsg ->
//            handleGamesCallback(gameList, errorMsg)
//        }
//    }

    fun createInvite(game: Game, onComplete: (Boolean) -> Unit) {
        repository.createInvite(game){ success ->
            onComplete(success)
        }
    }

//    private fun handleGamesCallback(gameList: List<Game>?, errorMsg: String?) {
//        if (gameList != null) {
//            _games.value = gameList
//            _error.value = null
//        } else {
//            _error.value = errorMsg
//        }
//    }

//    fun acceptInvite(game: Game, callback: (Boolean) -> Unit) {
//        repository.acceptInvite(game.id, callback)
//    }
//
//    fun cancelInvite(game: Game, callback: (Boolean) -> Unit) {
//        repository.cancelInvite(game.id, callback)
//    }
//
//    fun declineInvite(game: Game, callback: (Boolean) -> Unit) {
//        repository.declineInvite(game.id, callback)
//    }
//
//    fun cancelGame(game: Game, callback: (Boolean) -> Unit) {
//        repository.cancelGame(game.id) { success ->
//            callback(success)
//        }
//    }
//
//    fun getGameFromBookingCard(bookingCard: BookingCard): Game? {
//        return games.value?.find { game ->
//            (game.createdBy == bookingCard.otherPlayerName && game.sentTo == currentUserId) ||
//                    (game.sentTo == bookingCard.otherPlayerName && game.createdBy == currentUserId)
//        }
//    }
    // Fetch games near the current user's location, filter by future dates, and sort by date
    fun fetchGamesNearLocationToCurrentUser() {
        repository.getCurrentUserLocation { latitude, longitude ->
            repository.getGamesNearLocation(latitude, longitude, 100.0) { games ->
                val currentTime = Timestamp.now()

                // Filter games to include only those happening in the future
                val futureGames = games.filter { it.gameDateTime > currentTime }

                // Sort the filtered games by date in ascending order
                val sortedGames = futureGames.sortedBy { it.gameDateTime }

                // Post the sorted games to LiveData
                _upcomingGames.postValue(sortedGames)
            }
        }
    }


    // Add the current user to a game's participants
    fun participateInGame(gameId: String, userId: String) {
        repository.addUserToGameParticipants(gameId, userId) {
            // Callback after participation is successfully added
            val updatedGames = _upcomingGames.value?.map { game ->
                if (game.id == gameId) {
                    game.copy(participants = game.participants + userId) // Add user to participants
                } else {
                    game
                }
            } ?: emptyList() // Return an empty list if _upcomingGames.value is null

            _upcomingGames.postValue(updatedGames) // Update LiveData to refresh UI
        }
    }


}