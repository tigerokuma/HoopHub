package com.example.hoophubskeleton.fragment.TopMenu

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController


import com.example.hoophubskeleton.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.hoophubskeleton.viewmodel.AuthViewModel
import com.example.hoophubskeleton.factory.AuthViewModelFactory
import com.example.hoophubskeleton.repository.AuthRepository
import com.example.hoophubskeleton.repository.ProfileRepository
import com.example.hoophubskeleton.viewmodel.ProfileViewModel

//import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    // used to log out
    private lateinit var authViewModel: AuthViewModel
    // used to pull details from user profile
    private lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)

        // Initialize repositories and ViewModels
        val authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        val profileRepository = ProfileRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]
        profileViewModel = ProfileViewModel(profileRepository)


        // Observer to display user information
        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                view.findViewById<TextView>(R.id.profileName).text = user.name
                view.findViewById<TextView>(R.id.profileAge).text = user.age.toString()
                view.findViewById<TextView>(R.id.profileEmail).text = user.email
                view.findViewById<TextView>(R.id.profileCompetitionLevel).text =
                    user.competitionLevel
                view.findViewById<TextView>(R.id.profileLocation).text = user.location

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

        profileViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        // Getting profile data
        profileViewModel.fetchUserProfile()

        // Handle logout button click
        val logoutButton = view.findViewById<Button>(R.id.profileLogoutButton)
        logoutButton.setOnClickListener {
           showLogoutDialog()
        }

        // Handle edit profile button click
        val editButton = view.findViewById<Button>(R.id.profileEditButton)
        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

    }

    private fun showLogoutDialog() {
        val customView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
        val title = customView.findViewById<TextView>(R.id.customTitle)
        val message = customView.findViewById<TextView>(R.id.customMessage)
        val cancelButton = customView.findViewById<Button>(R.id.cancelButton)
        val confirmButton = customView.findViewById<Button>(R.id.confirmButton)

        title.text = "Logout"
        message.text = "Are you sure you want to logout?"
        cancelButton.text = "Cancel"
        confirmButton.text = "Logout"



        val dialog = AlertDialog.Builder(requireContext())
            .setView(customView)
            .create()

        // Set up button actions
        cancelButton.setOnClickListener {
            dialog.dismiss() // Close dialog when "Cancel" is clicked
        }

        confirmButton.setOnClickListener {
            authViewModel.logOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            dialog.dismiss() // Close dialog
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


}

