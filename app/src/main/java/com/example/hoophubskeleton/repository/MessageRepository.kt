package com.example.hoophubskeleton.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hoophubskeleton.model.Dialog
import com.example.hoophubskeleton.model.Message
import com.example.hoophubskeleton.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

//3eRRRRmcCpRW7Y1sDRrP24SEYUZ2
class MessageRepository {

    private val db = FirebaseFirestore.getInstance()

    // Fetch messages for a specific dialog
    fun getMessagesForDialog(dialogId: String): LiveData<List<Message>> {
        val liveData = MutableLiveData<List<Message>>()
        db.collection("dialogs").document(dialogId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = snapshot.toObjects(Message::class.java)
                    liveData.value = messages
                }
            }
        return liveData
    }

    // Send a message in a dialog
    fun sendMessage(dialogId: String, content: String, senderId: String) {
        val message = Message(
            senderId = senderId,
            content = content,
            timestamp = Timestamp.now()
        )

        db.collection("dialogs").document(dialogId).collection("messages")
            .add(message)
            .addOnSuccessListener {
                db.collection("dialogs").document(dialogId)
                    .update(
                        mapOf(
                            "latestMessage" to content,
                            "latestTimestamp" to Timestamp.now()
                        )
                    )
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Failed to update latest message: $e")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to send message: $e")
            }
    }

    // Create a new dialog
    fun createDialog(user1Id: String, user2Id: String, firstMessage: String, onComplete: (String) -> Unit) {
        val participants = listOf(user1Id, user2Id).sorted() // Ensure consistent order
        val newDialog = hashMapOf(
            "participants" to participants,
            "latestMessage" to firstMessage,
            "latestTimestamp" to Timestamp.now()
        )

        db.collection("dialogs")
            .add(newDialog)
            .addOnSuccessListener { documentReference ->
                onComplete(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to create dialog: $e")
            }
    }

    // Fetch dialogs for a user
    fun getDialogs(userId: String, onComplete: (List<Dialog>) -> Unit) {
        db.collection("dialogs")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching dialogs: $error")
                    onComplete(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val dialogs = mutableListOf<Dialog>()

                    snapshot.documents.forEach { document ->
                        val dialogId = document.id
                        val latestMessage = document.getString("latestMessage") ?: ""
                        val latestTimestamp = document.getTimestamp("latestTimestamp")
                        val participants = document.get("participants") as? List<String> ?: emptyList()

                        // Validate participants list
                        if (participants.isEmpty() || participants.any { it.isBlank() }) {
                            Log.e("Firestore", "Invalid participants list in dialog: $participants")
                            return@forEach
                        }

                        // Get the other participant ID (excluding current user)
                        val otherUserId = participants.firstOrNull { it != userId }

                        if (!otherUserId.isNullOrBlank()) {
                            // Fetch the other user's details
                            db.collection("users").document(otherUserId).get()
                                .addOnSuccessListener { userSnapshot ->
                                    val participantName = userSnapshot.getString("name") ?: "Unknown User"

                                    dialogs.add(
                                        Dialog(
                                            dialogId = dialogId,
                                            participants = participants,
                                            latestMessage = latestMessage,
                                            latestTimestamp = latestTimestamp
                                        )
                                    )

                                    // Ensure onComplete is triggered after processing all documents
                                    if (dialogs.size == snapshot.documents.size) {
                                        onComplete(dialogs)
                                    }
                                }
                                .addOnFailureListener { error ->
                                    Log.e("Firestore", "Failed to fetch user details: $error")
                                    // Add dialog without participant name in case of failure
                                    dialogs.add(
                                        Dialog(
                                            dialogId = dialogId,
                                            participants = participants,
                                            latestMessage = latestMessage,
                                            latestTimestamp = latestTimestamp
                                        )
                                    )

                                    if (dialogs.size == snapshot.documents.size) {
                                        onComplete(dialogs)
                                    }
                                }
                        } else {
                            // Add dialog if otherUserId is invalid
                            dialogs.add(
                                Dialog(
                                    dialogId = dialogId,
                                    participants = participants,
                                    latestMessage = latestMessage,
                                    latestTimestamp = latestTimestamp
                                )
                            )
                            if (dialogs.size == snapshot.documents.size) {
                                onComplete(dialogs)
                            }
                        }
                    }
                } else {
                    onComplete(emptyList())
                }
            }
    }

    // Fetch user details by ID
    fun getUserById(uid: String, onComplete: (User?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    onComplete(user)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
}