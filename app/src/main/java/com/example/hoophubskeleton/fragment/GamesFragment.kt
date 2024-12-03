package com.example.hoophubskeleton.fragment

import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.GameViewModel
import com.example.hoophubskeleton.adapter.GameAdapter
import com.example.hoophubskeleton.model.Game
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

import java.util.Locale

class GamesFragment : Fragment() {

    private lateinit var gamesRecyclerView: RecyclerView
    private lateinit var searchLocationEditText: EditText
    private lateinit var gamesViewModel: GameViewModel
    private lateinit var gamesAdapter: GameAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        // Initialize views
        gamesRecyclerView = view.findViewById(R.id.gamesRecyclerView)
        searchLocationEditText = view.findViewById(R.id.searchLocationEditText)

        // Initialize ViewModel
        gamesViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Set up RecyclerView
        gamesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe games from ViewModel
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            gamesViewModel.upcomingGames.observe(viewLifecycleOwner) { games ->
                // Filter out games the user is participating in
                val filteredGames = games.filter { game ->
                    !game.participants.contains(currentUserId)
                    game.participants.size < game.maxParticipants
                }
                gamesAdapter = GameAdapter(
                    context = requireContext(),
                    games = filteredGames,
                    currentUserId = currentUserId,
                    onParticipateClick = { game ->
                        // Handle participation
                        gamesViewModel.participateInGame(game.id, currentUserId)
                    }
                )
                gamesRecyclerView.adapter = gamesAdapter
            }
        } else {
            Toast.makeText(context, "Please log in to view games.", Toast.LENGTH_SHORT).show()
        }

        // Handle location-based search
        searchLocationEditText.setOnEditorActionListener { _, _, _ ->
            val locationName = searchLocationEditText.text.toString()
            if (locationName.isNotBlank()) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(locationName, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val latitude = location.latitude
                    val longitude = location.longitude
                    gamesViewModel.fetchGamesNearLocation(latitude, longitude)
                } else {
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                gamesViewModel.fetchGamesNearLocationToCurrentUser(requireActivity())
            }
            true
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Pass activity context explicitly
        gamesViewModel.fetchGamesNearLocationToCurrentUser(requireActivity())
    }



}


