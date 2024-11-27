package com.example.hoophubskeleton.repository

import com.example.hoophubskeleton.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun fetchUserProfile(callback: (User?, String?) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
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

    fun updateUserProfile(user: User, callback: (Boolean, String?) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).set(user)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } else {
            callback(false, "User not logged in")
        }
    }

}
