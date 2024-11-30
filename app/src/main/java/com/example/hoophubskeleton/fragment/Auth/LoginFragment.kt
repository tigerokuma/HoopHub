package com.example.hoophubskeleton.fragment.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.hoophubskeleton.MainActivity
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.AuthViewModel
import com.example.hoophubskeleton.factory.AuthViewModelFactory
import com.example.hoophubskeleton.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val authRepository =
            AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]

        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val goToSignUpButton: Button = view.findViewById(R.id.goToSignUpButton)

        // Navigate to SignUpFragment
        goToSignUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        // Observe authentication status from ViewModel
        authViewModel.authStatus.observe(viewLifecycleOwner) { (success, message) ->
            if (success) {
                // Navigate to MainFragment (hosting Players and Games tabs)
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            } else {
                Toast.makeText(requireContext(), "Login Failed: $message", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Login Failed. Please fill out both fields.",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d("LoginFragment", "Login failure: User email or password blank")
            } else {
                // Call the ViewModel to log in
                authViewModel.logIn(email, password)
            }
        }
    }
}
