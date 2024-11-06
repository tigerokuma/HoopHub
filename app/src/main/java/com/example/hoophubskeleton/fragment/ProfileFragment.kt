package com.example.hoophubskeleton.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.hoophubskeleton.EditDeleteProfile
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var authRepository: AuthRepository
    private lateinit var editButton: Button
    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var competitionTextView: TextView
    private lateinit var userLocation: TextView
    private lateinit var userProfilePic: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize non-UI related components (like ViewModels)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editButton = view.findViewById(R.id.profileEditButton)
        // Set up views, click listeners, or bind data here
        nameTextView = view.findViewById(R.id.profileName)
        ageTextView = view.findViewById(R.id.profileAge)
        emailTextView = view.findViewById(R.id.profileEmail)
        competitionTextView = view.findViewById(R.id.profileCompetitionLevel)
        userLocation = view.findViewById((R.id.profileLocation))
        userProfilePic = view.findViewById(R.id.profilePictureImageView)


        // Initialize AuthRepository with FirebaseAuth and Firestore instances
        authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        // Fetch user profile data
        loadUserProfile()




        editButton.setOnClickListener {
            // passing data to edit profile activity
            val intent = Intent(requireContext(), EditDeleteProfile::class.java)
            intent.putExtra("userName", nameTextView.text.toString())
            intent.putExtra("userAge", ageTextView.text.toString())
            intent.putExtra("userEmail", emailTextView.text.toString())
            intent.putExtra("userCompetition", competitionTextView.text.toString())
            intent.putExtra("userLocation", userLocation.text.toString())
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources related to views if necessary
    }

    private fun loadUserProfile() {
        // getting user data from repo
        authRepository.getUserProfile { user, error ->
            if (user != null) {
                // Populate the UI with user data
                nameTextView.text = user.name
                ageTextView.text = user.age.toString()
                emailTextView.text = user.email
                competitionTextView.text = user.competitionLevel
                userLocation.text = user.location
                // unsure of how to implement this
                user.profilePicUrl.let { url ->
                    Log.d("ProfileFrag", "Profile Picture URL: $url") // Log URL
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.default_profile_pic)
                        .error(R.drawable.profile_icon)
                        .into(userProfilePic)
                }
            } else {

                error?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}

