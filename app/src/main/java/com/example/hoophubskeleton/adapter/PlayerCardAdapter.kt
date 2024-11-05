package com.example.hoophubskeleton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.model.PlayerCard
import com.example.hoophubskeleton.R

// This is our RecyclerView.Adapter that connects our list of PlayerCard objects
// to the RecyclerView

class PlayerCardAdapter(private val playerList: List<PlayerCard>) : RecyclerView.Adapter<PlayerCardAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerImage = itemView.findViewById<ImageView>(R.id.playerImage)
        val playerName = itemView.findViewById<TextView>(R.id.playerName)
        val playerRating = itemView.findViewById<TextView>(R.id.playerRating)
        val playerLocation = itemView.findViewById<TextView>(R.id.playerLocation)
        val playerCompetitionLevel = itemView.findViewById<TextView>(R.id.playerCompetitionLevel)
        val inviteButton = itemView.findViewById<Button>(R.id.inviteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_card, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        // Based on the position, get the corresponding PlayerCard
        val playerCard = playerList[position]

        // Now use that playerCard to set the information in the ViewHolder
        holder.playerName.text = playerCard.name
        holder.playerRating.text = "${playerCard.rating}"
        holder.playerLocation.text = playerCard.location
        holder.playerCompetitionLevel.text = playerCard.competitionLevel
        holder.playerImage.setImageResource(playerCard.imageId)

        // ToDo: set up click listener for the invite button
        // holder.inviteButton.setOnClickListener{}
    }

}