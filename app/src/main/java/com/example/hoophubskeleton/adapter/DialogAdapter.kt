package com.example.hoophubskeleton.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.model.Dialog
import com.google.firebase.firestore.FirebaseFirestore

class DialogAdapter(
    dialogs: List<Dialog>,
    private val currentUserId: String, // Pass current user ID
    private val onDialogClick: (Dialog) -> Unit
) : RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    // Sort dialogs by timestamp (newer to older)
    private val sortedDialogs = dialogs.sortedByDescending { it.latestTimestamp }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog, parent, false)
        return DialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        val dialog = sortedDialogs[position]

        // Find the participant ID (excluding the current user ID)
        val participantId = dialog.participants.firstOrNull { it != currentUserId }

        if (participantId != null) {
            // Fetch participant name dynamically
            fetchParticipantName(participantId) { name ->
                holder.tvName.text = name // Dynamically set the participant's name
            }
        } else {
            holder.tvName.text = "Unknown User" // Fallback if no participant ID is found
        }

        // Bind latest message and timestamp
        holder.tvLatestMessage.text = dialog.latestMessage ?: "No messages yet"
        holder.tvTimestamp.text = dialog.latestTimestamp?.toDate()?.toString() ?: ""

        // Handle dialog click
        holder.itemView.setOnClickListener { onDialogClick(dialog) }
    }

    override fun getItemCount(): Int = sortedDialogs.size

    private fun fetchParticipantName(userId: String, onComplete: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("name") ?: "Unknown User"
                onComplete(name)
            }
            .addOnFailureListener {
                onComplete("Unknown User")
            }
    }

    inner class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvLatestMessage: TextView = itemView.findViewById(R.id.tvLatestMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }
}
