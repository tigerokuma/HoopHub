package com.example.hoophubskeleton.fragment.TopMenu

import ProfileViewModelFactory
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hoophubskeleton.R
import androidx.navigation.fragment.findNavController
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.hoophubskeleton.ProfileImageLauncher
import com.example.hoophubskeleton.ProfileUtil
import com.example.hoophubskeleton.ViewModel.AuthViewModel
import com.example.hoophubskeleton.factory.AuthViewModelFactory
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.AuthRepository
import com.example.hoophubskeleton.repository.ProfileRepository
import com.example.hoophubskeleton.ViewModel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class EditProfileFragment : Fragment() {
    private lateinit var updateButton: Button
    private lateinit var deleteButtton: Button
    private lateinit var cancelButtton: Button
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var emailEditText: TextView
    private lateinit var competitionEditGroup: RadioGroup
    private lateinit var locationEditText: EditText
    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var imageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            profileImageView.setImageURI(imageUri)
            val bitmap = imageUri?.let { ProfileUtil.getBitmapFromURI(requireContext(), it) }
            bitmap?.let { profileViewModel.tempUserImage.value = it }
            Log.d("EditProfileFragment", "Selected Image URI: $imageUri")
        } else {
            Log.e(
                "EditProfileFragment",
                "Image selection failed or canceled. Retaining current imageUri: $imageUri"
            )
        }
    }
    private val snappedPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("EditProfileFragment", "Camera result OK")
                Log.d("EditProfileFragment", "Image URI in result: $imageUri")

                if (imageUri != null) {
                    try {
                        Log.d("EditProfileFragment", "Attempting to create bitmap from URI")
                        val bitmap = ProfileUtil.getBitmapFromURI(requireContext(), imageUri!!)
                        bitmap?.let {
                            Log.d("EditProfileFragment", "Bitmap created successfully")
                            profileViewModel.tempUserImage.value = it
                        } ?: Log.e("EditProfileFragment", "Bitmap is null")
                    } catch (e: Exception) {
                        Log.e("EditProfileFragment", "Error creating bitmap: ${e.message}", e)
                    }
                } else {
                    Log.e("EditProfileFragment", "Image URI is null after camera result")
                }
            } else {
                Log.e("EditProfileFragment", "Camera result NOT OK: ${result.resultCode}")
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


        val authRepository =
            AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]
        val repository =
            ProfileRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        val factory = ProfileViewModelFactory(repository)
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
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
            ProfileUtil.checkPermissions(requireActivity())
            imageUri = createImageUri()
            ProfileImageLauncher(
                requireContext(),
                snappedPhotoLauncher,
                pickImageLauncher,
                imageUri
            ).launch()
        }

        profileViewModel.tempUserImage.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                Log.d("EditProfileFragment", "tempUserImage updated, setting bitmap to ImageView")
                profileImageView.setImageBitmap(bitmap)
            } else {
                Log.e("EditProfileFragment", "tempUserImage is null")
            }
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

        // Observing deletion status of profile
        profileViewModel.deleteStatus.observe(viewLifecycleOwner) { status ->
            val (success, message) = status
            if (success) {
                Toast.makeText(context, "Profile deleted successfully.", Toast.LENGTH_SHORT).show()
                // Navigate to login or another relevant screen
                findNavController().navigate(R.id.loginFragment)
            } else {
                Toast.makeText(context, "Failed to delete profile: $message", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Observing deletion status of credentials
        authViewModel.deleteStatus.observe(viewLifecycleOwner) { status ->
            val (success, message) = status
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Profile deleted successfully.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to delete profile: $message",
                    Toast.LENGTH_SHORT
                ).show()
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
                            Toast.makeText(
                                requireContext(),
                                "Password update failed: $message",
                                Toast.LENGTH_SHORT
                            ).show()
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

        deleteButtton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
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
                    Log.d(
                        "EditDeleteProfileFragment",
                        "Image uploaded successfully. Download URL: $uri"
                    )
                    onSuccess(uri.toString()) // Return the new download URL
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "EditDeleteProfileFragment",
                    "Failed to upload image: ${exception.message}",
                    exception
                )
            }
    }

    private fun updateUserProfile(profileViewModel: ProfileViewModel, updatedUser: User) {
        profileViewModel.updateUserProfile(updatedUser)

        profileViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), "Update failed: $error", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()
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
                        Log.e(
                            "EditProfileFragment",
                            "Failed to update password: $errorMessage",
                            task.exception
                        )
                        callback(false, errorMessage)
                    }
                }
        } else {
            Log.e("EditProfileFragment", "No user is logged in.")
            callback(false, "User is not logged in.")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("EditProfileFragment", "Updating indicator tab to ProfileFragment")
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.profileFragment)?.isChecked = true
        Log.e("EditProfileFragment", "R.id.profileFragment: ${R.id.profileFragment}")
        Log.e(
            "EditProfileFragment",
            "SelectedItem BottomNavMenu: ${bottomNavigationView.selectedItemId}"
        )


    }


    private fun showDeleteConfirmationDialog() {
        val customView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
        val title = customView.findViewById<TextView>(R.id.customTitle)
        val message = customView.findViewById<TextView>(R.id.customMessage)
        val cancelButton = customView.findViewById<Button>(R.id.cancelButton)
        val confirmButton = customView.findViewById<Button>(R.id.confirmButton)

        title.text = "Delete Profile"
        message.text =
            "We're sorry to see you go. Are you sure you want to delete your profile? This action cannot be undone."
        cancelButton.text = "Cancel"
        confirmButton.text = "Delete"


        val dialog = AlertDialog.Builder(requireContext())
            .setView(customView)
            .create()

        // Set up button actions
        cancelButton.setOnClickListener {
            dialog.dismiss() // Close dialog when "Cancel" is clicked
        }

        confirmButton.setOnClickListener {
            deletePlayer()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun deletePlayer() {
        // Trigger profile deletion
        profileViewModel.deleteUserProfile()

        // Trigger credentials deletion
        authViewModel.deleteUserCredentials()

        // Observe deletion statuses
        profileViewModel.deleteStatus.observe(viewLifecycleOwner) { (success, message) ->
            if (success) {
                Log.d("EditProfileFragment", "Profile deleted successfully.")
            } else {
                Log.e("EditProfileFragment", "Failed to delete profile: $message")
                Toast.makeText(
                    requireContext(),
                    "Failed to delete profile: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        authViewModel.deleteStatus.observe(viewLifecycleOwner) { (success, message) ->
            if (success) {
                Log.d("EditProfileFragment", "Credentials deleted successfully.")
                // Navigate to login fragment after both deletions succeed
                findNavController().navigate(R.id.loginFragment)
            } else {
                Log.e("EditProfileFragment", "Failed to delete credentials: $message")
                Toast.makeText(
                    requireContext(),
                    "Failed to delete credentials: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createImageUri(): Uri? {
        val imageFile = File(requireContext().getExternalFilesDir(null), "profile_image.jpg")
        Log.d("EditProfileFragment", "File exists: ${imageFile.exists()}, Path: ${imageFile.absolutePath}")
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", imageFile)
        Log.d("EditProfileFragment", "Created Image URI: $uri")
        return uri    }

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

}
