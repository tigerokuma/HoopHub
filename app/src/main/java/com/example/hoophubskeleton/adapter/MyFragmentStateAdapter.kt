package com.example.hoophubskeleton.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentStateAdapter(
    activity: FragmentActivity,
    private val fragmentList: List<Fragment> // Accept a list of fragments as a parameter
) : FragmentStateAdapter(activity) {

    override fun createFragment(itemPosition: Int): Fragment {
        return fragmentList[itemPosition] // Dynamically return the fragment based on position
    }

    override fun getItemCount(): Int {
        return fragmentList.size // Return the number of fragments in the list
    }
}
