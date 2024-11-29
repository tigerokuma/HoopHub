package com.example.hoophubskeleton.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData to observe authentication status and messages
    private val _authStatus = MutableLiveData<Pair<Boolean, String?>>()
    val authStatus: LiveData<Pair<Boolean, String?>>
        get() = _authStatus

    // LiveData for user profile and error message
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Sign-up function
    fun signUp(email: String, password: String, user: User) {
        authRepository.signUp(email, password, user) { success, message ->
            _authStatus.value = Pair(success, message)
        }
    }

    // Log-in function
    fun logIn(email: String, password: String) {
        authRepository.logIn(email, password) { success, message ->
            _authStatus.value = Pair(success, message)
        }
    }

    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean = authRepository.isLoggedIn()

    // Log out the user
    fun logOut() {
        authRepository.logOut()
    }

    // Function to fetch user profile data
    fun fetchUserProfile() {
        authRepository.getUserProfile { user, error ->
            if (user != null) {
                _userProfile.value = user
                _errorMessage.value = null
            } else {
                _userProfile.value = null
                _errorMessage.value = error
            }
        }
    }

    private val _deleteStatus = MutableLiveData<Pair<Boolean, String?>>()
    val deleteStatus: LiveData<Pair<Boolean, String?>> get() = _deleteStatus

    fun deleteUserCredentials() {
        authRepository.deleteUserCredentials { success, message ->
            _deleteStatus.value = Pair(success, message) // Update LiveData with the result
        }
    }
}
