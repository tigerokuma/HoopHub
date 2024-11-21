package com.example.hoophubskeleton.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.MessageViewModel
import com.example.hoophubskeleton.adapter.DialogAdapter
import com.example.hoophubskeleton.factory.MessageViewModelFactory
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.Navigation
import androidx.navigation.findNavController


//4YZaFWflCLbHNzLbXUxqGJwWU9f2

class InboxFragment : Fragment() {

    private lateinit var viewModel: MessageViewModel
    private lateinit var adapter: DialogAdapter
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth to get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserId = currentUser?.uid ?: return // Ensure the user is logged in

        // Initialize ViewModel
        val repository = MessageRepository()
        val viewModelFactory = MessageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MessageViewModel::class.java]

        // Set up RecyclerView
        val rvDialogs = view.findViewById<RecyclerView>(R.id.rvDialogs)
        rvDialogs.layoutManager = LinearLayoutManager(requireContext())

// Observe and display dialogs
        viewModel.getDialogs(currentUserId).observe(viewLifecycleOwner) { dialogs ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@observe
            val adapter = DialogAdapter(dialogs, currentUserId) { dialog ->
                val dialogId = dialog.dialogId

                // Safely get NavController
                val navController = findNavController()
                navController.navigate(R.id.action_global_to_inboxFragment)

                // Navigate only if NavController is available
                navController?.let {
                    val action = InboxFragmentDirections.actionInboxFragmentToChatFragment(dialogId)
                    it.navigate(action)
                }
            }
            rvDialogs.adapter = adapter
        }



        // Set up "Find User" button
        val findUserButton: Button = view.findViewById(R.id.btnFindUser)
        findUserButton.setOnClickListener {
            openFindUserDialog()
        }

        observeFoundUser()
    }



    private fun openFindUserDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_find_user, null)
        val uidInput = dialogView.findViewById<EditText>(R.id.etUserId)
        val findButton = dialogView.findViewById<Button>(R.id.btnFind)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Find User")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        findButton.setOnClickListener {
            val enteredUid = uidInput.text.toString().trim()
            if (enteredUid.isNotEmpty()) {
                viewModel.findUserById(enteredUid)
            } else {
                Toast.makeText(requireContext(), "Please enter a User ID", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeFoundUser() {
        viewModel.foundUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                showUserInfoDialog(user)
            } else {
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUserInfoDialog(user: User) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user_info, null)
        val userName = dialogView.findViewById<TextView>(R.id.tvUserName)
        val userEmail = dialogView.findViewById<TextView>(R.id.tvUserEmail)
        val messageInput = dialogView.findViewById<EditText>(R.id.etMessage)
        val sendButton = dialogView.findViewById<Button>(R.id.btnSendMessage)

        userName.text = user.name
        userEmail.text = user.email

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("User Info")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.createOrFetchDialog(currentUserId, user.uid, message)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
