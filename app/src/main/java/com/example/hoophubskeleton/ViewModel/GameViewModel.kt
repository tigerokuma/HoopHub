package com.example.hoophubskeleton.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.repository.GamesRepository
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.map
import com.example.hoophubskeleton.model.BookingCard
import com.example.hoophubskeleton.model.GameStatus
import com.google.firebase.auth.FirebaseAuth


class GameViewModel : ViewModel() {
    private val repository = GamesRepository(FirebaseFirestore.getInstance())

    // _games is mutable and only accessible to the viewmodel
    private val _games = MutableLiveData<List<Game>?>()
    // games is immutable and accessible to other fragments/activities
    val games: LiveData<List<Game>?> get() = _games

    // Only show valid games
    val activeGames: LiveData<List<Game>> = games.map { gameList ->
        gameList?.filter { it.status == GameStatus.PENDING || it.status == GameStatus.ACCEPTED } ?: emptyList()
    }

    private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User not logged in.")


    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error


    fun fetchAllGames() {
        repository.getAllGames { gameList, errorMsg ->
            handleGamesCallback(gameList, errorMsg)
        }
    }

    fun fetchGamesStartedByUser(userId: String) {
        repository.getGamesStartedByUser(userId) { gameList, errorMsg ->
            handleGamesCallback(gameList, errorMsg)
        }
    }

    fun fetchGamesUserInvitedTo(userId: String) {
        repository.getGamesUserInvitedTo(userId) { gameList, errorMsg ->
            handleGamesCallback(gameList, errorMsg)
        }
    }

    fun fetchAllGamesForUser(userId: String) {
        repository.getAllGamesForUser(userId) { gameList, errorMsg ->
            handleGamesCallback(gameList, errorMsg)
        }
    }

    fun createInvite(game: Game, onComplete: (Boolean) -> Unit) {
        repository.createInvite(game){ success ->
            onComplete(success)
        }
    }

    private fun handleGamesCallback(gameList: List<Game>?, errorMsg: String?) {
        if (gameList != null) {
            _games.value = gameList
            _error.value = null
        } else {
            _error.value = errorMsg
        }
    }

    fun acceptInvite(game: Game, callback: (Boolean) -> Unit) {
        repository.acceptInvite(game.id, callback)
    }

    fun cancelInvite(game: Game, callback: (Boolean) -> Unit) {
        repository.cancelInvite(game.id, callback)
    }

    fun declineInvite(game: Game, callback: (Boolean) -> Unit) {
        repository.declineInvite(game.id, callback)
    }

    fun cancelGame(game: Game, callback: (Boolean) -> Unit) {
        repository.cancelGame(game.id) { success ->
            callback(success)
        }
    }


    fun getGameFromBookingCard(bookingCard: BookingCard): Game? {
        return games.value?.find { game ->
            (game.createdBy == bookingCard.otherPlayerName && game.sentTo == currentUserId) ||
                    (game.sentTo == bookingCard.otherPlayerName && game.createdBy == currentUserId)
        }
    }


}