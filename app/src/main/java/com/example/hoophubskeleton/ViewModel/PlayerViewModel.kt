package com.example.hoophubskeleton.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.hoophubskeleton.model.PlayerCard
import com.example.hoophubskeleton.repository.PlayersRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.example.hoophubskeleton.model.User


class PlayerViewModel : ViewModel() {
    private val repository = PlayersRepository(FirebaseFirestore.getInstance())

    private val _users = MutableLiveData<List<User>?>()
    val users: LiveData<List<User>?> get() = _users

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    val playerCards: LiveData<List<PlayerCard>> = _users.map {userList ->
        userList?.map { user ->
            PlayerCard(
                uid = user.uid,
                name = user.name,
                location = user.location,
                imageId = user.profilePicUrl,
                competitionLevel = user.competitionLevel,
                profilePicUrl = user.profilePicUrl
            )
        } ?: emptyList()
    }

    init {
        fetchPlayers()
    }

    private fun fetchPlayers() {
        repository.getPlayers { playerList, errorMsg ->
            if(playerList != null) {
                _users.value = playerList
                _error.value = null
            } else {
                _error.value = errorMsg
            }
        }
    }

}