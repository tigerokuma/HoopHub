package com.example.hoophubskeleton.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.repository.GamesRepository
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.map


class GameViewModel : ViewModel() {
    private val repository = GamesRepository(FirebaseFirestore.getInstance())

    // _games is mutable and only accessible to the viewmodel
    private val _games = MutableLiveData<List<Game>?>()
    // games is immutable and accessible to other fragments/activities
    val games: LiveData<List<Game>?> get() = _games

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

}