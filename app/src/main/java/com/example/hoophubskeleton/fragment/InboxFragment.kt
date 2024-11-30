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
import androidx.navigation.NavController
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
import com.example.hoophubskeleton.adapter.SearchAdapter
import androidx.appcompat.widget.SearchView



//4YZaFWflCLbHNzLbXUxqGJwWU9f2

class InboxFragment : Fragment() {

    private lateinit var viewModel: MessageViewModel
    private lateinit var adapter: DialogAdapter
    private lateinit var currentUserId: String
    private lateinit var navController: NavController
    private var searchResultDialog: AlertDialog? = null // Keep a reference to the search result dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // Initialize FirebaseAuth to get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserId = currentUser?.uid.toString()

        if (currentUserId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize ViewModel
        val repository = MessageRepository()
        val viewModelFactory = MessageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MessageViewModel::class.java]

        // Set up RecyclerView
        val rvDialogs = view.findViewById<RecyclerView>(R.id.rvDialogs)
        rvDialogs.layoutManager = LinearLayoutManager(requireContext())

        // Observe and display dialogs
        viewModel.getDialogs(currentUserId).observe(viewLifecycleOwner) { dialogs ->
            adapter = DialogAdapter(dialogs, currentUserId) { dialog ->
                val dialogId = dialog.dialogId

                // Dismiss search result dialog (if open) when navigating
                searchResultDialog?.dismiss()

                // Use Safe Args to navigate to ChatFragment with dialogId
                val action = InboxFragmentDirections.actionInboxFragmentToChatFragment(dialogId)
                navController.navigate(action)
            }
            rvDialogs.adapter = adapter
        }

        // Set up SearchView for user search
        val searchView = view.findViewById<SearchView>(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchUsers(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Do nothing on text change (search only on submit)
                return false
            }
        })
    }

    private fun searchUsers(query: String) {
        if (query.isNotEmpty()) {
            viewModel.searchUsersByNameOrEmail(query).observe(viewLifecycleOwner) { users ->
                if (users.isNotEmpty()) {
                    showSearchResults(users)
                } else {
                    Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSearchResults(users: List<User>) {
        // Inflate the dialog view
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_search_results, null)

        // Create the dialog instance
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set up RecyclerView
        val rvSearchResults = dialogView.findViewById<RecyclerView>(R.id.rvSearchResults)
        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        val searchAdapter = SearchAdapter(users) { user ->
            // Dismiss the dialog before navigating
            dialog.dismiss()

            viewModel.createOrFetchDialog(currentUserId, user.uid) { dialogId ->
                val action = InboxFragmentDirections.actionInboxFragmentToChatFragment(dialogId)
                navController.navigate(action)
            }
        }
        rvSearchResults.adapter = searchAdapter

        // Set up Cancel button with clear focus management
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when Cancel is clicked
        }

        // Show the dialog
        dialog.show()
    }

}
