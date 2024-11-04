package com.example.hoophubskeleton

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

// manager/switches tabs in viewpager2 in mainactivity

// following code adapted from MyFragmentState adapter lab shown in class
// https://canvas.sfu.ca/courses/86002/pages/fragment-and-viewpager
class MyFragmentStateAdapter(activity: FragmentActivity, var fragmentArrayList: ArrayList<Fragment>)
    : FragmentStateAdapter(activity){

    override fun createFragment(itemPosition:Int): Fragment {
        return fragmentArrayList[itemPosition]
    }

    override fun getItemCount(): Int {
        // returns size of arraylist (i.e number of fragments)
        return fragmentArrayList.size
    }
}