package com.example.hoophubskeleton

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hoophubskeleton.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class EditDeleteProfile : AppCompatActivity() {
    private var clearInstanceState :Boolean = false
    private lateinit var updateButton: Button
    private lateinit var deleteButtton: Button
    private lateinit var cancelButtton: Button
    private lateinit var authRepository: AuthRepository
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var emailEditText: TextView
    private lateinit var competitionEditGroup: RadioGroup
    private lateinit var locationEditText: EditText
    private lateinit var userProfilePic: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_delete_profile)

        updateButton = findViewById(R.id.profileUpdateButton)
        deleteButtton = findViewById(R.id.profileDeleteButton)
        cancelButtton = findViewById(R.id.profileGoBack)

        authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        loadPassedUserData()


        cancelButtton.setOnClickListener {
            finish()

        }

        deleteButtton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        updateButton.setOnClickListener {
            Log.d("update: ", "update button clicked")
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().toIntOrNull() ?: 0
            val email = emailEditText.text.toString()
            val location = locationEditText.text.toString()
            val competitionLevel = when (competitionEditGroup.checkedRadioButtonId) {
                R.id.editBeginner -> "Beginner"
                R.id.editCasual -> "Casual"
                R.id.editCompetitive -> "Competitive"
                else -> ""
            }
            Log.d("update: ", "competition level: $competitionLevel")


            updateUserInFirestore(name, age, email, location, competitionLevel)
            finish()

        }


    }

    private fun loadPassedUserData() {
        nameEditText = findViewById(R.id.editProfileName)
        ageEditText = findViewById(R.id.editProfileAge)
        emailEditText = findViewById(R.id.editProfileEmail)
        competitionEditGroup = findViewById(R.id.editCompetitionLevelGroup)
        locationEditText = findViewById(R.id.editProfileLocation)

        // get data from Intent and set it
        val userName = intent.getStringExtra("userName")
        val userAge = intent.getStringExtra("userAge")
        val userEmail = intent.getStringExtra("userEmail")
        val userCompetition = intent.getStringExtra("userCompetition")
        when (userCompetition) {
            "Beginner" -> competitionEditGroup.check(R.id.editBeginner)
            "Casual" -> competitionEditGroup.check(R.id.editCasual)
            "Competitive" -> competitionEditGroup.check(R.id.editCompetitive)
        }
        val userLocation = intent.getStringExtra("userLocation")

        nameEditText.setText(userName)
        ageEditText.setText(userAge)
        emailEditText.text = userEmail
        locationEditText.setText(userLocation)
    }

    private fun deleteAccount() {
        /*
        Log.d("fragDelete:", "attempting to delete")

        val user = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        Log.d("fragDelete:", "attempting to delete user: ${user?.uid}")

        if (user != null) {
            Log.d("fragDelete:", "user not null")
            // delete user data from firestore
            firestore.collection("users").document(user.uid).delete()
                .addOnCompleteListener { task ->
                    Log.d("fragDelete:", "oncompletelistener")
                    if (task.isSuccessful) {
                        Log.d("fragDelete:", "Firestore document deletion successful")

                        // delete user's authentication account
                        user.delete().addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                FirebaseAuth.getInstance().signOut()

                                // clear related cache and files
                                cacheDir.deleteRecursively()
                                filesDir.deleteRecursively()

                                FirebaseFirestore.getInstance().clearPersistence()
                                clearInstanceState = true
                                Log.d("fragDelete:", "Account Deleted")
                                restartApp()
                            } else {
                                // failure in deleting the firbase auth account
                                Log.e("fragDelete:", "Failed to delete authentication account", deleteTask.exception)
                            }
                        }.addOnFailureListener { exception ->
                            // log exception from account deletion
                            Log.e("fragDelete:", "Error deleting authentication account", exception)
                        }
                    } else {
                        // log exception deleting user data from firestore
                        Log.e("fragDelete:", "Failed to delete user data from Firestore", task.exception)
                    }
                }.addOnFailureListener { exception ->
                    // log exception deleting firestore document
                    Log.e("fragDelete:", "Error deleting Firestore document", exception)
                }
        } else {
            Log.d("fragDelete:", "Cannot delete null user")
        }
         */
    }


    override fun onSaveInstanceState(outState: Bundle) {
        if (clearInstanceState) {
            return
        }
        super.onSaveInstanceState(outState)
    }

    private fun restartApp() {
        // restart app from fresh
        val intent = Intent(this, AuthHostActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        Runtime.getRuntime().exit(0)
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Profile")
        builder.setMessage("We're sorry to see you go. Are you sure you want to delete your profile? This action cannot be undone.")

        // delete
        builder.setPositiveButton("Delete") { dialog, _ ->
            dialog.dismiss()
            deleteAccount()
            finishAffinity()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateUserInFirestore(
        name: String,
        age: Int,
        email: String,
        location: String,
        competitionLevel: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        if (user != null) {
            // create map of updated fields
            val updatedFields = mapOf(
                "name" to name,
                "age" to age,
                "email" to email,
                "location" to location,
                "competitionLevel" to competitionLevel
            )

            // update firestore document
            firestore.collection("users").document(user.uid)
                .update(updatedFields)
                .addOnSuccessListener {
                    Log.d("EditDeleteProfile", "User profile updated successfully.")
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("EditDeleteProfile", "Failed to update profile", e)
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("EditDeleteProfile", "User not logged in.")
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun reauthenticateUser(email: String, currentPassword: String, onReauthenticated: () -> Unit) {
        // used to change password
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EditDeleteProfile", "User re-authenticated.")
                onReauthenticated()
            } else {
                Log.e("EditDeleteProfile", "Re-authentication failed: ${task.exception?.message}")
                Toast.makeText(this, "Re-authentication failed. Please check your password.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun updateUserPassword(newPassword: String) {
        // used to update password
        val user = FirebaseAuth.getInstance().currentUser

        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EditDeleteProfile", "User password updated successfully.")
                Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("EditDeleteProfile", "Password update failed: ${task.exception?.message}")
                Toast.makeText(this, "Failed to update password.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}









