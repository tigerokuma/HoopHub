package com.example.hoophubskeleton.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.model.Game
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class GameAdapter(
    private val context: Context,
    private val games: List<Game>,
    private val currentUserId: String, // Pass the current user ID
    private val onParticipateClick: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    // Filter games that are still upcoming
    private val upcomingGames = games
        .filter { it.gameDateTime > Timestamp.now() } // Only future games
        .sortedBy { it.gameDateTime } // Sort by nearest date

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameDateTimeTextView: TextView = itemView.findViewById(R.id.gameDateTimeTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val skillLevelTextView: TextView = itemView.findViewById(R.id.skillLevelTextView)
        val participantsCountTextView: TextView = itemView.findViewById(R.id.participantsCountTextView) // New
        val participateButton: Button = itemView.findViewById(R.id.participateButton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = upcomingGames[position]

        // Set date and skill level
        holder.gameDateTimeTextView.text = "Date: ${game.gameDateTime.toDate()}"
        holder.skillLevelTextView.text = "Skill Level: ${game.skillLevel}"

        // Set number of participants
        holder.participantsCountTextView.text = "Participants: ${game.participants.size}"

        // Convert GeoPoint to an address
        val location = game.location
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            holder.locationTextView.text = if (!addresses.isNullOrEmpty()) {
                "Location: ${addresses[0].getAddressLine(0)}"
            } else {
                "Location: Address not found"
            }

            // Add click listener to open Google Maps
            holder.locationTextView.setOnClickListener {
                val address = if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0)
                } else {
                    "${location.latitude}, ${location.longitude}"
                }
                val geoUri = "geo:${location.latitude},${location.longitude}?q=${Uri.encode(address)}"
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                mapIntent.setPackage("com.google.android.apps.maps") // Open in Google Maps app if available
                it.context.startActivity(mapIntent)
            }

            // Add underline and clickable styling
            holder.locationTextView.apply {
                setTextColor(context.getColor(android.R.color.holo_blue_light)) // Use built-in blue
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG // Add underline
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.locationTextView.text = "Location: Unable to fetch address"
            // Add a default click listener if the address is unavailable
            holder.locationTextView.setOnClickListener {
                val geoUri = "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                mapIntent.setPackage("com.google.android.apps.maps")
                it.context.startActivity(mapIntent)
            }

            // Add underline and clickable styling for fallback
            holder.locationTextView.apply {
                setTextColor(context.getColor(android.R.color.holo_blue_light)) // Use built-in blue
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG // Add underline
            }
        }

        // Handle participation button
        if (game.participants.contains(currentUserId)) {
            holder.participateButton.text = "Already Participating"
            holder.participateButton.isEnabled = false
        } else {
            holder.participateButton.text = "Participate"
            holder.participateButton.isEnabled = true
            holder.participateButton.setOnClickListener {
                onParticipateClick(game) // Trigger participation logic
            }
        }
    }


    override fun getItemCount(): Int {
        return upcomingGames.size
    }
}