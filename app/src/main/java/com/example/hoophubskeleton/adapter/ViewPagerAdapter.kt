package com.example.hoophubskeleton.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hoophubskeleton.fragment.GamesFragment
import com.example.hoophubskeleton.fragment.TopMenu.PlayersFragment

//cant come back to user fragment and inbox 
class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2 // Number of tabs: Players and Games

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlayersFragment() // Players tab
            1 -> GamesFragment()   // Games tab
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
