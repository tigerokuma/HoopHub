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
import com.example.hoophubskeleton.viewmodel.GameViewModel
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
        // Create dummy games at the beginning
       // createDummyGames()

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
                gamesAdapter = GameAdapter(
                    context = requireContext(),
                    games = games,
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
                gamesViewModel.fetchGamesNearLocationToCurrentUser()
            }
            true
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Fetch games near current user location by default
        gamesViewModel.fetchGamesNearLocationToCurrentUser()
    }

    fun createDummyGames() {
        val firestore = FirebaseFirestore.getInstance()
        val sanFranciscoLocation = GeoPoint(37.7749, -122.4194) // GeoPoint for SF location

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val games = listOf(
            Game(
                id = firestore.collection("games").document().id,
                gameDateTime = Timestamp(dateFormat.parse("2025-01-05 10:30:00")!!), // Jan 5, 2025, 10:30 AM
                location = sanFranciscoLocation,
                skillLevel = "Beginner",
                participants = mutableListOf()
            ),
            Game(
                id = firestore.collection("games").document().id,
                gameDateTime = Timestamp(dateFormat.parse("2025-01-12 15:45:00")!!), // Jan 12, 2025, 3:45 PM
                location = sanFranciscoLocation,
                skillLevel = "Intermediate",
                participants = mutableListOf()
            ),
            Game(
                id = firestore.collection("games").document().id,
                gameDateTime = Timestamp(dateFormat.parse("2025-01-20 18:00:00")!!), // Jan 20, 2025, 6:00 PM
                location = sanFranciscoLocation,
                skillLevel = "Pro",
                participants = mutableListOf()
            ),
            Game(
                id = firestore.collection("games").document().id,
                gameDateTime = Timestamp(dateFormat.parse("2025-01-25 13:15:00")!!), // Jan 25, 2025, 1:15 PM
                location = sanFranciscoLocation,
                skillLevel = "Casual",
                participants = mutableListOf()
            ),
            Game(
                id = firestore.collection("games").document().id,
                gameDateTime = Timestamp(dateFormat.parse("2025-01-31 23:59:00")!!), // Jan 31, 2025, 11:59 PM
                location = sanFranciscoLocation,
                skillLevel = "Advanced",
                participants = mutableListOf()
            )
        )

        games.forEach { game ->
            firestore.collection("games").document(game.id).set(game)
        }
    }


}
