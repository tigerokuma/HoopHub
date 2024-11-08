package com.example.hoophubskeleton.fragment

import android.content.Intent
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
import com.example.hoophubskeleton.AuthHostActivity

import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.AuthViewModel
import com.example.hoophubskeleton.factory.AuthViewModelFactory
import com.example.hoophubskeleton.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import coil.load
import coil.request.CachePolicy

//import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)

        // Initialize ViewModel
        val authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]

        // Observe userProfile to display user information
        authViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Update UI with user data
                view.findViewById<TextView>(R.id.profileName).text = user.name
                view.findViewById<TextView>(R.id.profileAge).text = user.age.toString()
                view.findViewById<TextView>(R.id.profileEmail).text = user.email
                view.findViewById<TextView>(R.id.profileCompetitionLevel).text = user.competitionLevel
                view.findViewById<TextView>(R.id.profileLocation).text = user.location

                // Load profile picture
                profileImageView.load(user.profilePicUrl) {
                    placeholder(R.drawable.default_profile_pic)
                    error(R.drawable.default_profile_pic)
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }

            }
        }

        // Observe errorMessage for any issues with retrieving user data
        authViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch user profile data when the fragment is created
        authViewModel.fetchUserProfile()

        // Handle logout button click
        val logoutButton = view.findViewById<Button>(R.id.profileLogoutButton)
        logoutButton.setOnClickListener {
            authViewModel.logOut()
            // Redirect to AuthHostActivity after logout
            val intent = Intent(requireContext(), AuthHostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Handle edit profile button click
        val editButton = view.findViewById<Button>(R.id.profileEditButton)
        editButton.setOnClickListener {
            // edit profile logic here
        }

        // Handle delete profile button click
        val deleteButton = view.findViewById<Button>(R.id.profileDeleteButton)
        deleteButton.setOnClickListener {
            // delete logic backend here
        }
    }
}

