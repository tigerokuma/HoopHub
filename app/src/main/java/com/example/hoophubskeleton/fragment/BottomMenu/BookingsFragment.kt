package com.example.hoophubskeleton.fragment.BottomMenu

import android.os.Bundle
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
//import com.example.hoophubskeleton.adapter.BookingCardAdapter
import com.example.hoophubskeleton.model.BookingCard
import com.example.hoophubskeleton.model.CardType
import com.google.firebase.auth.FirebaseAuth
import com.example.hoophubskeleton.model.Game
//import com.example.hoophubskeleton.model.GameStatus
import com.example.hoophubskeleton.model.PlayerCard

class BookingFragment : Fragment() {
  /*  private val gameViewModel : GameViewModel by viewModels()
    private val playerViewModel : PlayerViewModel by viewModels()

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

        bookingCardAdapter = BookingCardAdapter(emptyList()) { bookingCard, action ->
            handleBookingCardClick(bookingCard, action)
        }
        recyclerView.adapter = bookingCardAdapter

        gameViewModel.fetchAllGamesForUser(currentUserId)

        observeData()
    }

    private fun observeData() {
        gameViewModel.games.observe(viewLifecycleOwner) { games ->
            playerViewModel.playerCards.observe(viewLifecycleOwner) { players ->
                if (games != null && players != null) {
                    val bookingCards = createBookingCards(games, players)
                    bookingCardAdapter.updateList(bookingCards)
                } else {
                    // Handle the case where either games or players is null
                    bookingCardAdapter.updateList(emptyList())
                }
            }
        }
    }


    private fun createBookingCards(
        games: List<Game>,
        players: List<PlayerCard>
    ): List<BookingCard> {
        return games.mapNotNull { game ->
            val otherPlayerId = getOtherPlayerId(game)
            val player = players.find { it.uid == otherPlayerId }

            player?.let { mapGameToBookingCard(game, it) }
        }
    }

    // Helper function to get the other player's ID
    private fun getOtherPlayerId(game: Game): String {
        return if (game.createdBy == currentUserId) game.sentTo else game.createdBy
    }

    // Helper function to map Game and PlayerCard to BookingCard
    private fun mapGameToBookingCard(game: Game, player: PlayerCard): BookingCard? {
        val cardType = when (game.status) {
            GameStatus.PENDING -> if (game.createdBy == currentUserId) {
                CardType.PENDING_SENT
            } else {
                CardType.PENDING_RECEIVED
            }
            GameStatus.ACCEPTED -> CardType.ACCEPTED
            GameStatus.CANCELLED -> null
            GameStatus.DECLINED -> null
        }

        // If cardType is null (e.g., CANCELLED), don't create a BookingCard
        return cardType?.let {
            BookingCard(
                otherPlayerName = player.name,
                otherPlayerImageUrl = player.profilePicUrl,
                competitionLevel = player.competitionLevel,
                location = game.location.toString(),
                dateTime = game.gameDateTime.toString(),
                cardType = it
            )
        }

    }


    enum class Action {
        CANCEL_INVITE,
        ACCEPT_INVITE,
        DECLINE_INVITE,
        CANCEL_GAME
    }

    // This function will handle the different actions the one (or two) buttons on the card can take

    private fun handleBookingCardClick(bookingCard: BookingCard, action: Action) {
        val game = gameViewModel.getGameFromBookingCard(bookingCard)
        if (game == null) {
            Toast.makeText(requireContext(), "Game not found.", Toast.LENGTH_SHORT).show()
            return
        }

        when (action) {
            Action.CANCEL_INVITE -> {
                gameViewModel.cancelInvite(game) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Invite canceled.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to cancel invite.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Action.ACCEPT_INVITE -> {
                gameViewModel.acceptInvite(game) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Invite accepted!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to accept invite.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Action.DECLINE_INVITE -> {
                gameViewModel.declineInvite(game) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Invite declined.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to decline invite.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Action.CANCEL_GAME -> {
                gameViewModel.cancelGame(game) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Game canceled.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to cancel game.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    */

}
