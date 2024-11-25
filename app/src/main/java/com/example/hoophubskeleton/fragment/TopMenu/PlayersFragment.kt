package com.example.hoophubskeleton.fragment.TopMenu

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
import com.example.hoophubskeleton.fragment.InviteBottomSheetFragment
import com.google.firebase.auth.FirebaseAuth

class PlayersFragment : Fragment() {
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        val adapter = PlayerCardAdapter(emptyList()) { playerCard ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                val inviteBottomSheetFragment = InviteBottomSheetFragment.newInstance(currentUserId, playerCard.uid)
                inviteBottomSheetFragment.show(parentFragmentManager, "InviteBottomSheetFragment")
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        playerViewModel.playerCards.observe(viewLifecycleOwner, { playerCards ->
            adapter.updateList(playerCards)
        })

        playerViewModel.error.observe(viewLifecycleOwner, { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources related to views if necessary
    }
}