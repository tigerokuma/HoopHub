package com.example.hoophubskeleton.fragment.TopMenu

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.model.PlayerCard
import com.example.hoophubskeleton.adapter.PlayerCardAdapter
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.MessageViewModel
import com.example.hoophubskeleton.ViewModel.PlayerViewModel
import com.example.hoophubskeleton.factory.MessageViewModelFactory
import com.example.hoophubskeleton.fragment.InviteBottomSheetFragment
import com.example.hoophubskeleton.model.User
import com.example.hoophubskeleton.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth

class PlayersFragment : Fragment() {
    private val playerViewModel: PlayerViewModel by viewModels()
    private lateinit var messageViewModel: MessageViewModel // Change to lateinit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MessageViewModel using the factory
        val repository = MessageRepository() // Ensure MessageRepository is properly set up
        val factory = MessageViewModelFactory(repository)
        messageViewModel = ViewModelProvider(this, factory).get(MessageViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView -- this will show our player cards
        val recyclerView = view.findViewById<RecyclerView>(R.id.playerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter setup
        val adapter = PlayerCardAdapter(
            emptyList(),
            onInviteClick = { playerCard ->
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId != null) {
                    val inviteBottomSheetFragment = InviteBottomSheetFragment.newInstance(currentUserId, playerCard.uid)
                    inviteBottomSheetFragment.show(parentFragmentManager, "InviteBottomSheetFragment")
                } else {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            },
            onMessageClick = { playerCard ->
                val user = User(
                    uid = playerCard.uid,
                    name = playerCard.name
                )
                showUserInfoDialog(user) // Call the function to display the dialog
            }
        )
        recyclerView.adapter = adapter

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        // Observers
        playerViewModel.playerCards.observe(viewLifecycleOwner, { playerCards ->
            // Exclude current user from list
            val filteredPlayerCards = playerCards.filter {it.uid != currentUserId}
            adapter.updateList(filteredPlayerCards)
        })

        playerViewModel.error.observe(viewLifecycleOwner, { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    // Corrected function using MessageViewModel
    private fun showUserInfoDialog(user: User) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user_info, null)
        val userName = dialogView.findViewById<TextView>(R.id.tvUserName)
        val messageInput = dialogView.findViewById<EditText>(R.id.etMessage)
        val charCountText = dialogView.findViewById<TextView>(R.id.tvCharacterCount) // TextView for countdown
        val sendButton = dialogView.findViewById<Button>(R.id.btnSendMessage)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancel)

        userName.text = user.name

        // Set maxLength for the message
        val maxLength = 500

        // Add TextWatcher to update character count
        messageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                charCountText.text = "$length/$maxLength"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Create the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Send Private Message")
            .setView(dialogView)
            .create()

        // Send button logic
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (message.length > maxLength) {
                Toast.makeText(requireContext(), "Message exceeds the character limit", Toast.LENGTH_SHORT).show()
            } else {
                // Call the MessageViewModel to handle message creation
                messageViewModel.createOrFetchDialog(
                    currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    otherUserId = user.uid,
                    message = message
                ) { dialogId ->
                    Toast.makeText(requireContext(), "Message sent successfully.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Dismiss the dialog after success
                }
            }
        }

        // Cancel button logic
        cancelButton.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when Cancel is clicked
        }

        dialog.show()
    }

}
