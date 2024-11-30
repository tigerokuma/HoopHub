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

    fun sendMessage(dialogId: String, content: String, senderId: String) {
        val message = Message(
            senderId = senderId,
            content = content,
            timestamp = com.google.firebase.Timestamp.now()
        )

        // Add the message to the messages subcollection
        db.collection("dialogs").document(dialogId).collection("messages")
            .add(message)
            .addOnSuccessListener {
                // Update the latestMessage and latestTimestamp in the dialog document
                db.collection("dialogs").document(dialogId)
                    .update(
                        mapOf(
                            "latestMessage" to content,
                            "latestTimestamp" to com.google.firebase.Timestamp.now()
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


    fun createDialog(user1Id: String, user2Id: String, firstMessage: String, onComplete: (String) -> Unit) {
        val participants = listOf(user1Id, user2Id).sorted() // Ensure consistent order
        val newDialog = hashMapOf(
            "participants" to participants,
            "latestMessage" to firstMessage,
            "latestTimestamp" to com.google.firebase.Timestamp.now()
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




    fun getDialogs(userId: String, onComplete: (List<Dialog>) -> Unit) {
        db.collection("dialogs")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onComplete(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val dialogs = mutableListOf<Dialog>()
                    val tasks = snapshot.documents.map { document ->
                        val dialogId = document.id
                        val latestMessage = document.getString("latestMessage") ?: ""
                        val latestTimestamp = document.getTimestamp("latestTimestamp")
                        val participants = document.get("participants") as? List<String> ?: emptyList()

                        // Find the other participant ID (exclude current user ID)
                        val otherUserId = participants.firstOrNull { it != userId }

                        if (otherUserId != null) {
                            // Fetch the participant's name from the `users` collection
                            db.collection("users").document(otherUserId).get()
                                .addOnSuccessListener { userSnapshot ->
                                    val participantName = userSnapshot.getString("name") ?: "Unknown User"

                                    // Add the dialog to the list with the fetched name
                                    dialogs.add(
                                        Dialog(
                                            dialogId = dialogId,
                                            participants = participants,
                                            latestMessage = latestMessage,
                                            latestTimestamp = latestTimestamp
                                        )
                                    )

                                    // Update the dialog in the adapter once all names are fetched
                                    if (dialogs.size == snapshot.documents.size) {
                                        onComplete(dialogs)
                                    }
                                }
                                .addOnFailureListener {
                                    // Handle failures gracefully
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
                    }
                } else {
                    onComplete(emptyList())
                }
            }
    }




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



    fun searchUsers(query: String, onComplete: (List<User>) -> Unit) {
        val lowerCaseQuery = query.lowercase()

        // Query by name (case-insensitive)
        val nameQuery = db.collection("users")
            .whereGreaterThanOrEqualTo("name", lowerCaseQuery)
            .whereLessThanOrEqualTo("name", lowerCaseQuery + "\uf8ff")

        // Query by email (case-insensitive)
        val emailQuery = db.collection("users")
            .whereGreaterThanOrEqualTo("email", lowerCaseQuery)
            .whereLessThanOrEqualTo("email", lowerCaseQuery + "\uf8ff")

        // Combine results
        nameQuery.get().addOnSuccessListener { nameSnapshot ->
            emailQuery.get().addOnSuccessListener { emailSnapshot ->
                val users = mutableSetOf<User>()

                // Add users from both queries
                users.addAll(nameSnapshot.toObjects(User::class.java))
                users.addAll(emailSnapshot.toObjects(User::class.java))

                onComplete(users.toList()) // Return combined results as a list
            }.addOnFailureListener {
                onComplete(emptyList()) // Handle email query failure
            }
        }.addOnFailureListener {
            onComplete(emptyList()) // Handle name query failure
        }
    }


    fun createOrFetchDialog(currentUserId: String, otherUserId: String, onComplete: (String) -> Unit) {
        db.collection("dialogs")
            .whereArrayContains("participants", currentUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                val existingDialog = snapshot.documents.firstOrNull { document ->
                    val participants = document.get("participants") as? List<String>
                    participants?.contains(otherUserId) == true
                }

                if (existingDialog != null) {
                    onComplete(existingDialog.id)
                } else {
                    val newDialog = hashMapOf(
                        "participants" to listOf(currentUserId, otherUserId),
                        "latestMessage" to "",
                        "latestTimestamp" to Timestamp.now()
                    )
                    db.collection("dialogs")
                        .add(newDialog)
                        .addOnSuccessListener { documentReference ->
                            onComplete(documentReference.id)
                        }
                        .addOnFailureListener {
                            onComplete("")
                        }
                }
            }
    }



}