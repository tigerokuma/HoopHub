package com.example.hoophubskeleton

import android.content.Intent
import android.net.Uri
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
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_delete_profile)

        updateButton = findViewById(R.id.profileUpdateButton)
        deleteButtton = findViewById(R.id.profileDeleteButton)
        cancelButtton = findViewById(R.id.profileGoBack)

        //authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        //loadPassedUserData()


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









