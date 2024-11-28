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
import java.text.SimpleDateFormat
import java.util.Locale

class BookingCardAdapter(
    private var bookingList: List<BookingCard>,
    private val handleBookingCardClick: (BookingCard) -> Unit
) : RecyclerView.Adapter<BookingCardAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantsCount: TextView = itemView.findViewById(R.id.participantsCount)
        val gameDate: TextView = itemView.findViewById(R.id.gameDate)
        val gameTime: TextView = itemView.findViewById(R.id.gameTime)
        val gameLocation: TextView = itemView.findViewById(R.id.gameLocation)
        val leaveGameButton: Button = itemView.findViewById(R.id.leaveGameButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_card, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val bookingCard = bookingList[position]

        // Format date and time
        val dateTime = bookingCard.dateTime.toDate()
        val formattedDate = SimpleDateFormat("MMMM dd", Locale.getDefault()).format(dateTime)
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime)

        holder.gameDate.text = formattedDate
        holder.gameTime.text = formattedTime

        // Set participants
        val participantsCountText = "${bookingCard.participantNames.size}/${bookingCard.maxParticipants}"
        holder.participantsCount.text = participantsCountText

        // Display location as GeoPoint
        val geoPoint = bookingCard.location
        holder.gameLocation.text = "Lat: ${geoPoint.latitude}, Lng: ${geoPoint.longitude}"

        // Handle button click
        holder.leaveGameButton.setOnClickListener {
            handleBookingCardClick(bookingCard)
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    fun updateList(newList: List<BookingCard>) {
        bookingList = newList
        notifyDataSetChanged()
    }
}
