package com.example.hoophubskeleton

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlayersFragment : Fragment() {override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Initialize non-UI related components (like ViewModels)
}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up views, click listeners, or bind data here

        // Set up RecyclerView -- this will show our player cards
        val recyclerView = view.findViewById<RecyclerView>(R.id.playerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data for PlayerCard items
        val samplePlayers = listOf(
            PlayerCard("Michael", 5.0, "Wilmington, North Carolina", R.drawable.players_icon, "Competitive"),
            PlayerCard("Larry", 4.2, "French Lick, Indiana", R.drawable.players_icon, "Casual"),
            PlayerCard("Russell", 1.0, "Long Beach, California", R.drawable.players_icon, "Beginner" )
        )

        // Initialize adapter using sample list and set it on RecyclerView
        val adapter = PlayerCardAdapter(samplePlayers)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources related to views if necessary
    }
}