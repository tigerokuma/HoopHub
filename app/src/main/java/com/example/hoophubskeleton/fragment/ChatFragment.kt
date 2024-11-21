package com.example.hoophubskeleton.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.MessageViewModel
import com.example.hoophubskeleton.adapter.MessageAdapter
import com.example.hoophubskeleton.factory.MessageViewModelFactory
import com.example.hoophubskeleton.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {
    private lateinit var viewModel: MessageViewModel
    private lateinit var dialogId: String
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve dialogId from arguments
        arguments?.let {
            dialogId = ChatFragmentArgs.fromBundle(it).dialogId
        }
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

        // Find views by ID
        val rvMessages = view.findViewById<RecyclerView>(R.id.rvMessages)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etMessage = view.findViewById<EditText>(R.id.etMessage)

        // Set up RecyclerView
        rvMessages.layoutManager = LinearLayoutManager(requireContext())

        // Observe messages and set up the adapter
        viewModel.getMessages(dialogId).observe(viewLifecycleOwner) { messages ->
            val adapter = MessageAdapter(messages, currentUserId)
            rvMessages.adapter = adapter
        }

        // Handle send button click
        btnSend.setOnClickListener {
            val messageContent = etMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                viewModel.sendMessage(dialogId, messageContent, currentUserId)
                etMessage.text.clear()
            }
        }
    }

}