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

class PlayersFragment : Fragment() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.playerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter for player cards
        fun onInviteClick() {
            val inviteBottomSheetFragment = InviteBottomSheetFragment()
            inviteBottomSheetFragment.show(parentFragmentManager, "InviteBottomSheetFragment")
        }

        val adapter = PlayerCardAdapter(emptyList(), ::onInviteClick)
        recyclerView.adapter = adapter

        // Observe ViewModel for data
        playerViewModel.playerCards.observe(viewLifecycleOwner) { playerCards ->
            adapter.updateList(playerCards)
        }

        playerViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources related to views if necessary
    }
}
