package com.example.hoophubskeleton.repository
import com.example.hoophubskeleton.model.User
import com.google.firebase.firestore.FirebaseFirestore

class PlayersRepository(private val firestore: FirebaseFirestore) {

    /**
     * Fetch a list of all players asynchronously.
     * The callback provides the list of players or an error message.
     */
    fun getPlayers(callback: (List<User>?, String?) -> Unit) {
        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                val players = result.toObjects(User::class.java)
                callback(players, null)
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    /**
     * Find a user by their UID.
     * Returns a single User object or null if not found.
     */
    fun getUserById(uid: String, callback: (User?) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null) // User not found
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    /**
     * Search for users by name or email.
     * Returns a list of users matching the query.
     */
    fun searchUsers(query: String, callback: (List<User>) -> Unit) {
        firestore.collection("users")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val users = result.toObjects(User::class.java)
                callback(users)
            }
            .addOnFailureListener {
                callback(emptyList()) // Return empty list on failure
            }
    }

    /**
     * Fetch existing player-related dialogs or activities.
     * This method can be extended for specific use cases as needed.
     */
    fun getPlayerDialogs(playerId: String, callback: (List<String>?) -> Unit) {
        firestore.collection("dialogs")
            .whereArrayContains("participants", playerId)
            .get()
            .addOnSuccessListener { result ->
                val dialogIds = result.documents.mapNotNull { it.id }
                callback(dialogIds)
            }
            .addOnFailureListener {
                callback(null) // Return null if something goes wrong
            }
    }
}