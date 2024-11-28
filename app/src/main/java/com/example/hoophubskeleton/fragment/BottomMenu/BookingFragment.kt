package com.example.hoophubskeleton.fragment.BottomMenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.ViewModel.GameViewModel
import com.example.hoophubskeleton.ViewModel.PlayerViewModel
import com.example.hoophubskeleton.adapter.BookingCardAdapter
import com.example.hoophubskeleton.model.BookingCard
import com.google.firebase.auth.FirebaseAuth
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.model.PlayerCard

class BookingFragment : Fragment() {
    private val gameViewModel: GameViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by viewModels()

    private lateinit var bookingCardAdapter: BookingCardAdapter
    private val currentUserId: String by lazy {
        FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.bookingRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookingCardAdapter = BookingCardAdapter(emptyList()) { bookingCard ->
            handleBookingCardClick(bookingCard)
        }
        recyclerView.adapter = bookingCardAdapter

        // Listen for real-time updates
        gameViewModel.listenToGamesForUser(currentUserId)
        observeData()
    }


    private fun observeData() {
        gameViewModel.games.observe(viewLifecycleOwner) { games ->
            Log.d("PaulTest, BookingsFragment", "Fetched games: ${games?.size ?: 0}")
            playerViewModel.playerCards.observe(viewLifecycleOwner) { players ->
                Log.d("PaulTest, BookingsFragment", "Fetched players: ${players?.size ?: 0}")
                if (games != null && players != null) {
                    val bookingCards = createBookingCards(games, players)
                    Log.d("PaulTest, BookingsFragment", "Booking cards count: ${bookingCards.size}")
                    bookingCardAdapter.updateList(bookingCards)
                } else {
                    bookingCardAdapter.updateList(emptyList())
                }
            }
        }
    }




    private fun createBookingCards(games: List<Game>, players: List<PlayerCard>): List<BookingCard> {
        return games.mapNotNull { game ->
            val participantCards = game.participants.mapNotNull { userId ->
                players.find { it.uid == userId }
            }
            val participantNames = participantCards.map { it.name }
            val participantImages = participantCards.map { it.profilePicUrl }

            BookingCard(
                gameId = game.id,
                participantNames = participantNames,
                participantImages = participantImages,
                location = game.location,
                dateTime = game.gameDateTime,
                competitionLevel = game.skillLevel,
                maxParticipants = game.maxParticipants,
                courtName = game.courtName
            )
        }
    }

    private fun handleBookingCardClick(bookingCard: BookingCard) {
        val gameId = bookingCard.gameId
        gameViewModel.leaveGame(gameId, currentUserId) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Left game successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to leave game.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
