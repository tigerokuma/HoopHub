package com.example.hoophubskeleton.ViewModel

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.model.Dialog
import com.example.hoophubskeleton.model.Message
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.MessageRepository
import kotlinx.coroutines.launch

//4zBOLlofmtWHV0JS43E3IGu6EVI2
class MessageViewModel(private val repository: MessageRepository) : ViewModel() {

    private val _foundUser = MutableLiveData<User?>()
    val foundUser: LiveData<User?> get() = _foundUser

    // Fetch user by ID
    fun findUserById(uid: String) {
        viewModelScope.launch {
            repository.getUserById(uid) { user ->
                _foundUser.postValue(user)
            }
        }
    }

    // Use the repository's createOrFetchDialog function
    fun createOrFetchDialog(
        currentUserId: String,
        otherUserId: String,
        message: String? = null, // Make message optional
        onComplete: (String) -> Unit
    ) {
        repository.createOrFetchDialog(currentUserId, otherUserId) { dialogId ->
            if (dialogId.isNotEmpty() && !message.isNullOrEmpty()) {
                // Only send a message if a message is provided
                repository.sendMessage(dialogId, message, currentUserId)
            }
            onComplete(dialogId)
        }
    }

    // Fetch all dialogs for a user
    fun getDialogs(userId: String): LiveData<List<Dialog>> {
        val dialogsLiveData = MutableLiveData<List<Dialog>>()
        repository.getDialogs(userId) { dialogs ->
            dialogsLiveData.postValue(dialogs)
        }
        return dialogsLiveData
    }

    // Fetch all messages for a specific dialog
    fun getMessages(dialogId: String): LiveData<List<Message>> {
        return repository.getMessagesForDialog(dialogId)
    }

    // Send a message within a dialog
    fun sendMessage(dialogId: String, content: String, senderId: String) {
        repository.sendMessage(dialogId, content, senderId)
    }

    // Search for users by name or email
    fun searchUsersByNameOrEmail(query: String): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        repository.searchUsers(query) { users ->
            liveData.postValue(users)
        }
        return liveData
    }
}
