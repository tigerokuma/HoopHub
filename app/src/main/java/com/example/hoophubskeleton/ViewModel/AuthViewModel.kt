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
}
