package com.example.hoophubskeleton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.compose.material3.Card
import androidx.recyclerview.widget.LinearLayoutManager
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
        // val geoPoint = bookingCard.location
        holder.gameLocation.text = bookingCard.courtName

        // Handle button click
        holder.leaveGameButton.setOnClickListener {
            handleBookingCardClick(bookingCard)
        }

        holder.participantsCount.setOnClickListener {
            showParticipantsPopup(holder.participantsCount, bookingCard.participantNames)
        }

    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    fun updateList(newList: List<BookingCard>) {
        bookingList = newList
        notifyDataSetChanged()
    }

    private fun showParticipantsPopup(anchor: View, participants: List<String>) {
        val context = anchor.context
        val popupView = LayoutInflater.from(context).inflate(R.layout.participant_list_popup, null)
        val participantList = popupView.findViewById<RecyclerView>(R.id.participantList)

        // Set up RecyclerView with participants
        participantList.layoutManager = LinearLayoutManager(context)
        participantList.adapter = ParticipantAdapter(participants)

        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f
        popupWindow.showAsDropDown(anchor, 0, 0)
    }

}
