package com.example.hoophubskeleton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.material3.Card
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.fragment.BottomMenu.BookingFragment
import com.example.hoophubskeleton.model.BookingCard
import com.example.hoophubskeleton.model.CardType
/*
class BookingCardAdapter(
    private var bookingList: List<BookingCard>,
    private val handleBookingCardClick: (BookingCard, BookingFragment.Action) -> Unit
    ) : RecyclerView.Adapter<BookingCardAdapter.BookingViewHolder>() {
    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerImage: ImageView = itemView.findViewById(R.id.playerImage)
        val playerName: TextView = itemView.findViewById(R.id.playerName)
        val competitionLevel: TextView = itemView.findViewById(R.id.playerCompetitionLevel)
        val location: TextView = itemView.findViewById(R.id.gameLocation)
        val dateTime: TextView = itemView.findViewById(R.id.gameDateTime)
        val bookingButton1: Button = itemView.findViewById(R.id.bookingButton1)
        val bookingButton2: Button = itemView.findViewById(R.id.bookingButton2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_card, parent, false)
        return BookingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val bookingCard = bookingList[position]

        holder.playerName.text = bookingCard.otherPlayerName
        holder.competitionLevel.text = bookingCard.competitionLevel
        holder.location.text = bookingCard.location
        holder.dateTime.text = bookingCard.dateTime

        // Load image using Coli
        if (bookingCard.otherPlayerImageUrl.isNullOrBlank()) {
            holder.playerImage.setImageResource(R.drawable.default_profile_pic)
        } else {
            holder.playerImage.load(bookingCard.otherPlayerImageUrl)
        }

        // Logic to determine what buttons to show
        when (bookingCard.cardType) {
            CardType.PENDING_SENT -> {
                holder.bookingButton1.text = "Cancel"
                holder.bookingButton2.visibility = View.GONE // Hide the second button
                holder.bookingButton1.setOnClickListener {
                    handleBookingCardClick(bookingCard, BookingFragment.Action.CANCEL_INVITE)
                }
            }

            CardType.PENDING_RECEIVED -> {
                holder.bookingButton1.text = "Accept"
                holder.bookingButton2.text = "Decline"
                holder.bookingButton2.visibility = View.VISIBLE // Show both buttons
                holder.bookingButton1.setOnClickListener {
                    handleBookingCardClick(bookingCard, BookingFragment.Action.ACCEPT_INVITE)
                }
                holder.bookingButton2.setOnClickListener {
                    handleBookingCardClick(bookingCard, BookingFragment.Action.DECLINE_INVITE)
                }
            }

            CardType.ACCEPTED -> {
                holder.bookingButton1.text = "Cancel"
                holder.bookingButton2.visibility = View.GONE // Hide the second button
                holder.bookingButton1.setOnClickListener {
                    handleBookingCardClick(bookingCard, BookingFragment.Action.CANCEL_GAME)
                }
            }
        }
    }


    fun updateList(newList: List<BookingCard>) {
        bookingList = newList
        notifyDataSetChanged()
    }

}

*/