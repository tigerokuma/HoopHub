package com.example.hoophubskeleton.fragment.Auth

import android.app.Activity
import android.app.Activity.RESULT_OK
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hoophubskeleton.ProfileImageLauncher
import com.example.hoophubskeleton.ProfileUtil
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.AuthViewModel
import com.example.hoophubskeleton.factory.AuthViewModelFactory
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.view.isEmpty

class SignUpFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private val pickImageRequest = 1
    private var imageUri: Uri? = null
    private lateinit var profilePictureImageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val snappedPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                profilePictureImageView.setImageURI(imageUri)
                Log.d("SignupFragment:", "Camera result OK")
            }
        }


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
        profilePictureImageView = view.findViewById(R.id.profilePictureImageView)
        val nameEditText: EditText = view.findViewById(R.id.nameEditText)
        val ageEditText: EditText = view.findViewById(R.id.ageEditText)
        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val competitionLevelGroup: RadioGroup = view.findViewById(R.id.competitionLevelGroup)
        val locationEditText: EditText = view.findViewById(R.id.locationEditText)
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
        profilePictureImageView.setOnClickListener {
            if (ProfileUtil.hasPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                imageUri = createImageUri()
                ProfileImageLauncher(
                    requireContext(),
                    snappedPhotoLauncher,
                    pickImageLauncher,
                    imageUri
                ).launch()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(
                        requireContext(),
                        "Camera and gallery permissions are needed to select a profile picture.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                ProfileUtil.checkPermissions(activity)
            }
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

            // Validate inputs
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (age == 0) {
                Toast.makeText(requireContext(), "Age cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Email cannot be empty.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Password cannot be empty.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (competitionLevelGroup.isEmpty()) {
                Toast.makeText(requireContext(), "Competition level cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (location.isEmpty()) {
                Toast.makeText(requireContext(), "Location cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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

        // Observe the sign-up status
        authViewModel.authStatus.observe(viewLifecycleOwner) { (success, message) ->
            if (success) {
                // Navigate to MainFragment upon successful sign-up
                Toast.makeText(
                    requireContext(),
                    "Sign-up successful!",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
            } else {
                // Show error message if sign-up fails
                Toast.makeText(requireContext(), "Sign-up failed: $message", Toast.LENGTH_SHORT)
                    .show()
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

    private fun createImageUri(): Uri? {
        val imageFile = File(requireContext().getExternalFilesDir(null), "profile_image.jpg")
        Log.d(
            "EditProfileFragment",
            "File exists: ${imageFile.exists()}, Path: ${imageFile.absolutePath}"
        )
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
        Log.d("EditProfileFragment", "Created Image URI: $uri")
        return uri
    }

    private fun deleteTempImageFile() {
        // Remove camera temp image
        imageUri?.let { uri ->
            val file = File(uri.path ?: return)
            if (file.exists()) {
                val deleted = file.delete()
                Log.d("EditProfileFragment", "Temporary image file deleted: $deleted")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteTempImageFile()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (granted) {
                Toast.makeText(requireContext(), "Permissions granted.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permissions denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}