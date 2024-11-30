package com.example.hoophubskeleton.ViewModel

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

    private val _deleteStatus = MutableLiveData<Pair<Boolean, String?>>()
    val deleteStatus: LiveData<Pair<Boolean, String?>> get() = _deleteStatus

    fun deleteUserProfile() {
        profileRepository.deleteUserProfile { success, error ->
            if (success) {
                _userProfile.value = null // Clear the profile data from LiveData
                _deleteStatus.value = Pair(true, null) // Notify success to the UI
            } else {
                _deleteStatus.value = Pair(false, error) // Notify failure to the UI
            }
        }
    }

}
