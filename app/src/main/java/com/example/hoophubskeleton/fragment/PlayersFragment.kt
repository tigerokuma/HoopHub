package com.example.hoophubskeleton.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.model.PlayerCard
import com.example.hoophubskeleton.adapter.PlayerCardAdapter
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.PlayerViewModel

class PlayersFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val playerViewModel: PlayerViewModel by viewModels()

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

        val adapter = PlayerCardAdapter(emptyList())
        recyclerView.adapter = adapter

        playerViewModel.playerCards.observe(viewLifecycleOwner, { playerCards ->
            adapter.updateList(playerCards)
        })

        playerViewModel.error.observe(viewLifecycleOwner, { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        // Dummy data for PlayerCard items
//        val samplePlayers = listOf(
//            PlayerCard("Michael", 5.0, "Wilmington, North Carolina", R.drawable.players_icon, "Competitive"),
//            PlayerCard("Larry", 4.2, "French Lick, Indiana", R.drawable.players_icon, "Casual"),
//            PlayerCard("Russell", 1.0, "Long Beach, California", R.drawable.players_icon, "Beginner" )
//        )
//
//        // Initialize adapter using sample list and set it on RecyclerView
//        val adapter = PlayerCardAdapter(samplePlayers)
//        recyclerView.adapter = adapter
//    }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources related to views if necessary
    }
}