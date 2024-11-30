package com.example.hoophubskeleton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hoophubskeleton.R
import com.example.hoophubskeleton.model.User

class SearchAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val userEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = user.name
        holder.userEmail.text = user.email

        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}
