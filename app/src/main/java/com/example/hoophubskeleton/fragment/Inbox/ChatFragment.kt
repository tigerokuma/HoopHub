package com.example.hoophubskeleton.fragment.Inbox

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.MessageViewModel
import com.example.hoophubskeleton.adapter.ChatAdapter
import com.example.hoophubskeleton.factory.MessageViewModelFactory
import com.example.hoophubskeleton.repository.MessageRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    private lateinit var viewModel: MessageViewModel
    private lateinit var dialogId: String
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve dialogId from Safe Args
        dialogId = arguments?.let {
            ChatFragmentArgs.fromBundle(it).dialogId
        } ?: throw IllegalArgumentException("Missing dialogId argument")

        // Retrieve the current user ID
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val repository = MessageRepository()
        val viewModelFactory = MessageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MessageViewModel::class.java]

        // Set up UI components
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etMessage = view.findViewById<EditText>(R.id.etMessage)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val titleName = view.findViewById<TextView>(R.id.titleName)
        val tvCharAndWordCount = view.findViewById<TextView>(R.id.tvCharAndWordCount) // Added for character and word count
        val chatBackground = view.findViewById<ImageView>(R.id.chatBackground)

        // Configure RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up back button functionality
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Fetch and set the name of the person we're texting
        fetchAndSetParticipantName(titleName)

        // Observe messages and update RecyclerView
        viewModel.getMessages(dialogId).observe(viewLifecycleOwner) { messages ->
            val adapter = ChatAdapter(messages, currentUserId)
            recyclerView.adapter = adapter
            recyclerView.scrollToPosition(messages.size - 1) // Scroll to the latest message
        }

        // Add TextWatcher to update character and word count
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0 // Character count
                val wordCount = if (s.isNullOrBlank()) 0 else s.trim().split("\\s+".toRegex()).size // Word count
                tvCharAndWordCount.text = "$charCount/800 characters"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle send button click
        btnSend.setOnClickListener {
            val messageContent = etMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                viewModel.sendMessage(dialogId, messageContent, currentUserId)
                etMessage.text.clear() // Clear the input field
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchAndSetParticipantName(titleName: TextView) {
        // Fetch the dialog from Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("dialogs").document(dialogId).get()
            .addOnSuccessListener { dialogSnapshot ->
                val participants = dialogSnapshot.get("participants") as? List<String>
                val otherUserId = participants?.firstOrNull { it != currentUserId }

                if (!otherUserId.isNullOrEmpty()) {
                    // Use ViewModel to fetch the user's details
                    viewModel.findUserById(otherUserId)
                    viewModel.foundUser.observe(viewLifecycleOwner) { user ->
                        if (user != null) {
                            titleName.text = user.name // Assuming 'name' is a field in the User model
                        } else {
                            titleName.text = "Unknown User"
                        }
                    }
                } else {
                    titleName.text = "Unknown User"
                }
            }
            .addOnFailureListener {
                titleName.text = "Error fetching user"
            }
    }


    override fun onResume() {
        // changes indicator icon to inbox
        super.onResume()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.menu?.findItem(R.id.inboxFragment)?.isChecked = true
    }
}
