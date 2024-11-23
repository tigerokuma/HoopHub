package com.example.hoophubskeleton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.model.Message

class ChatAdapter(private val messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tvMessageSent)
        private val timestampText: TextView = itemView.findViewById(R.id.tvTimestampSent)

        fun bind(message: Message) {
            messageText.text = message.content
            timestampText.text = message.timestamp.toString()
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tvMessageReceived)
        private val timestampText: TextView = itemView.findViewById(R.id.tvTimestampReceived)

        fun bind(message: Message) {
            messageText.text = message.content
            timestampText.text = message.timestamp.toString()
        }
    }
}
