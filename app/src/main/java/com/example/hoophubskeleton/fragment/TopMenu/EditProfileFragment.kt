package com.example.hoophubskeleton.fragment.TopMenu

import ProfileViewModelFactory
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import com.example.hoophubskeleton.R
import androidx.navigation.fragment.findNavController
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.ProfileRepository
import com.example.hoophubskeleton.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfileFragment : Fragment() {
    private lateinit var updateButton: Button
    private lateinit var deleteButtton: Button
    private lateinit var cancelButtton: Button
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var emailEditText: TextView
    private lateinit var competitionEditGroup: RadioGroup
    private lateinit var locationEditText: EditText
    private var imageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            profileImageView.setImageURI(imageUri)
            Log.d("EditProfileFragment", "Selected Image URI: $imageUri")
        } else {
            Log.e("EditProfileFragment", "Image selection failed or canceled. Retaining current imageUri: $imageUri")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val repository = ProfileRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        val factory = ProfileViewModelFactory(repository)
        val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        profileViewModel.fetchUserProfile()

        updateButton = view.findViewById(R.id.profileUpdateButton)
        deleteButtton = view.findViewById(R.id.profileDeleteButton)
        cancelButtton = view.findViewById(R.id.profileGoBack)

        profileImageView = view.findViewById<ImageView>(R.id.editProfilePictureImageView)
        nameEditText = view.findViewById(R.id.editProfileName)
        ageEditText = view.findViewById(R.id.editProfileAge)
        emailEditText = view.findViewById(R.id.editProfileEmail)
        competitionEditGroup = view.findViewById(R.id.editCompetitionLevelGroup)
        locationEditText = view.findViewById(R.id.editProfileLocation)

        profileImageView.setOnClickListener {
            openGallery()
        }

        // Observer to display user information
        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                nameEditText.setText(user.name)
                ageEditText.setText(user.age.toString())
                emailEditText.text = user.email
                val competionLevel = user.competitionLevel
                when (competionLevel) {
                    "Beginner" -> competitionEditGroup.check(R.id.editBeginner)
                    "Casual" -> competitionEditGroup.check(R.id.editCasual)
                    "Competitive" -> competitionEditGroup.check(R.id.editCompetitive)
                }
                locationEditText.setText(user.location)

                // Load profile picture
                profileImageView.load(user.profilePicUrl) {
                    placeholder(R.drawable.default_profile_pic)
                    error(R.drawable.default_profile_pic)
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    transformations(CircleCropTransformation())
                }
            }
        }



        updateButton.setOnClickListener {
            val newPassword = view.findViewById<EditText>(R.id.editProfilePassword).text.toString()

            uploadImageToFirebaseStorage { downloadUrl ->
                // If no new image is uploaded, retain the current profile picture URL
                val profilePictureUrl = if (downloadUrl.isEmpty()) {
                    profileViewModel.userProfile.value?.profilePicUrl ?: ""
                } else {
                    downloadUrl
                }

                val updatedUser = User(
                    name = nameEditText.text.toString(),
                    age = ageEditText.text.toString().toIntOrNull() ?: 0,
                    email = emailEditText.text.toString(),
                    competitionLevel = when (competitionEditGroup.checkedRadioButtonId) {
                        R.id.editBeginner -> "Beginner"
                        R.id.editCasual -> "Casual"
                        R.id.editCompetitive -> "Competitive"
                        else -> ""
                    },
                    location = locationEditText.text.toString(),
                    profilePicUrl = profilePictureUrl
                )

                // Only update the password if the field is not empty
                if (newPassword.isNotEmpty()) {
                    updatePassword(newPassword) { isSuccess, message ->
                        if (isSuccess) {
                            // Update the user profile after password change
                            updateUserProfile(profileViewModel, updatedUser)
                        } else {
                            Toast.makeText(requireContext(), "Password update failed: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Directly update the user profile if no password change is required
                    updateUserProfile(profileViewModel, updatedUser)
                }
            }
        }



        cancelButtton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }


    private fun uploadImageToFirebaseStorage(onSuccess: (String) -> Unit) {
        if (imageUri == null) {
            Log.d("EditDeleteProfileFragment", "imageUri is null, no image to upload.")
            onSuccess("") // Pass an empty string or handle it appropriately.
            return
        }

        // Check if the URI is a Firebase URL (already uploaded)
        val uriString = imageUri.toString()
        if (uriString.startsWith("https://")) {
            Log.d("EditDeleteProfileFragment", "Using existing Firebase URL: $uriString")
            onSuccess(uriString) // Skip upload and return the existing URL
            return
        }

        // Upload new image if it's a local URI
        val storageReference = FirebaseStorage.getInstance()
            .getReference("profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("EditDeleteProfileFragment", "Image uploaded successfully. Download URL: $uri")
                    onSuccess(uri.toString()) // Return the new download URL
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EditDeleteProfileFragment", "Failed to upload image: ${exception.message}", exception)
            }
    }

    private fun updateUserProfile(profileViewModel: ProfileViewModel, updatedUser: User) {
        profileViewModel.updateUserProfile(updatedUser)

        profileViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), "Update failed: $error", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Navigate back to the previous fragment
            }
        }
    }


    private fun updatePassword(newPassword: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("EditProfileFragment", "Password updated successfully.")
                        callback(true, null)
                    } else {
                        val errorMessage = task.exception?.message
                        Log.e("EditProfileFragment", "Failed to update password: $errorMessage", task.exception)
                        callback(false, errorMessage)
                    }
                }
        } else {
            Log.e("EditProfileFragment", "No user is logged in.")
            callback(false, "User is not logged in.")
        }
    }




}
