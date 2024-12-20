package com.example.hoophubskeleton.repository

import com.example.hoophubskeleton.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun signUp(email: String, password: String, user: User, callback: (Boolean, String?) -> Unit) {
        // Create user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the Firebase UID
                    val uid = task.result?.user?.uid
                    if (uid != null) {
                        // Add the UID to the user model
                        val userWithUid = user.copy(uid = uid)
                        // Save the user data to Firestore
                        firestore.collection("users").document(uid)
                            .set(userWithUid)
                            .addOnSuccessListener {
                                callback(true, null) // Sign-up successful
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.message) // Firestore error
                            }
                    } else {
                        callback(false, "User ID is null") // No UID, should not happen normally
                    }
                } else {
                    callback(false, task.exception?.message) // Authentication error
                }
            }
    }

    fun logIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null) // Log-in successful
                } else {
                    callback(false, task.exception?.message) // Log-in error
                }
            }
    }

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun logOut() = firebaseAuth.signOut()

    // get profile data for Profilefragment
    fun getUserProfile(callback: (User?, String?) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // user data passed to callback
                        val user = document.toObject(User::class.java)
                        callback(user, null)
                    } else {
                        callback(null, "No user data found")
                    }
                }
                .addOnFailureListener { e ->
                    callback(null, e.message)
                }
        } else {
            callback(null, "User not logged in")
        }
    }

    fun deleteUserCredentials(callback: (Boolean, String?) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        val user = firebaseAuth.currentUser
        if (uid != null && user != null) {
            // delete user document in FirebaseAuth
            firestore.collection("users").document(uid)
                .delete()
                .addOnSuccessListener {
                    // delete user authentication account
                    user.delete()
                        .addOnSuccessListener {
                            callback(true, null) // Success
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception.message) // Failure
                        }
                }
                .addOnFailureListener { exception ->
                    callback(false, exception.message) // Failure with error message
                }
        } else {
            callback(false, "User not logged in")
        }
    }



}
