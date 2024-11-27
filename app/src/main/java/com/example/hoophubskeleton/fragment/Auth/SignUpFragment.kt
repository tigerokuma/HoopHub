package com.example.hoophubskeleton.fragment.Auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.AuthViewModel
import com.example.hoophubskeleton.factory.AuthViewModelFactory
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SignUpFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private val pickImageRequest = 1
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val authRepository =
            AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]

        val nameEditText: EditText = view.findViewById(R.id.nameEditText)
        val ageEditText: EditText = view.findViewById(R.id.ageEditText)
        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val competitionLevelGroup: RadioGroup = view.findViewById(R.id.competitionLevelGroup)
        val locationEditText: EditText = view.findViewById(R.id.locationEditText)
        val profilePictureImageView: ImageView = view.findViewById(R.id.profilePictureImageView)
        val chooseProfilePicButton: Button = view.findViewById(R.id.chooseProfilePicButton)
        val signUpButton: Button = view.findViewById(R.id.signUpButton)
        val goToLoginButton: Button = view.findViewById(R.id.goToLoginButton)

        // Navigate to LoginFragment
        goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // Initialize the ActivityResultLauncher for image selection
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    imageUri = result.data?.data
                    profilePictureImageView.setImageURI(imageUri)
                    Log.d("SignUpFragment", "Selected Image URI: $imageUri")
                } else {
                    Log.e("SignUpFragment", "Image selection failed or canceled.")
                }
            }

        // Handle profile picture selection
        chooseProfilePicButton.setOnClickListener {
            openGallery()
        }
        // Handle sign-up button click
        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().toIntOrNull() ?: 0
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val location = locationEditText.text.toString()
            val competitionLevel = when (competitionLevelGroup.checkedRadioButtonId) {
                R.id.radioBeginner -> "Beginner"
                R.id.radioCasual -> "Casual"
                R.id.radioCompetitive -> "Competitive"
                else -> ""
            }

            uploadImageToFirebaseStorage { profilePicUrl ->
                val user = User(
                    uid = "",
                    name = name,
                    age = age,
                    email = email,
                    competitionLevel = competitionLevel,
                    location = location,
                    profilePicUrl = profilePicUrl ?: ""
                )

                authViewModel.signUp(email, password, user)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun uploadImageToFirebaseStorage(callback: (String?) -> Unit) {
        if (imageUri == null) {
            Log.e("SignUpFragment", "Image URI is null. Please select an image.")
            callback(null)
            return
        }

        val storageReference =
            FirebaseStorage.getInstance().getReference("profile_pics/${UUID.randomUUID()}")
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("SignUpFragment", "Uploaded Profile Picture URL: $uri") // Log URL

                    callback(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SignUpFragment", "Failed to upload image: ${exception.message}")
                callback(null)
            }
    }

}