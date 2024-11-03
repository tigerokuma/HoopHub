package com.example.hoophubskeleton

import android.content.Context
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentSetup (
    private val context: Context,
    private val topTabMenu: TabLayout,
    private val viewPager: ViewPager2,
    private val tabItems: Array<String>,
    private val tabIcons: TypedArray
) {
    fun setupFragments(fragments: ArrayList<Fragment>) {
        // Initialize the FragmentAdapter with the fragments list
        val fragmentStateAdapter = MyFragmentStateAdapter((context as AppCompatActivity), fragments)
        viewPager.adapter = fragmentStateAdapter

        // Set up TabLayoutMediator to link TabLayout with ViewPager2
        TabLayoutMediator(topTabMenu, viewPager) { tab, position ->
            // Set up tab text and icon
            tab.text = tabItems[position]
            tab.setIcon(tabIcons.getResourceId(position, -1))
            val iconColor = ContextCompat.getColor(context, R.color.text_icon_dark)
            tab.icon?.setTint(iconColor)
        }.attach()
    }

}