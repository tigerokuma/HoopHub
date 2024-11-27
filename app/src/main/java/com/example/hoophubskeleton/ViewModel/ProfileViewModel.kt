package com.example.hoophubskeleton.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.ProfileRepository

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchUserProfile() {
        profileRepository.fetchUserProfile { user, error ->
            if (user != null) {
                _userProfile.value = user
                _errorMessage.value = null
            } else {
                _userProfile.value = null
                _errorMessage.value = error
            }
        }
    }

    fun updateUserProfile(user: User) {
        profileRepository.updateUserProfile(user) { success, error ->
            if (success) {
                _userProfile.value = user
                _errorMessage.value = null
            } else {
                _errorMessage.value = error
            }
        }
    }

}
