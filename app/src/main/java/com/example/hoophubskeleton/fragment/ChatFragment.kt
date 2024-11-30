package com.example.hoophubskeleton.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
       // val rvMessages = view.findViewById<RecyclerView>(R.id.rvMessages)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etMessage = view.findViewById<EditText>(R.id.etMessage)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)


        // Configure RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        // Observe messages and update RecyclerView
        viewModel.getMessages(dialogId).observe(viewLifecycleOwner) { messages ->
            val adapter = ChatAdapter(messages, currentUserId)
            recyclerView.adapter = adapter
            recyclerView.scrollToPosition(messages.size - 1) // Scroll to the latest message
        }



        // Handle send button click
        btnSend.setOnClickListener {
            val messageContent = etMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                viewModel.sendMessage(dialogId, messageContent, currentUserId)
                etMessage.text.clear()
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
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
