package com.example.hoophubskeleton.fragment

import android.content.Context
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.hoophubskeleton.adapter.MyFragmentStateAdapter
import com.example.hoophubskeleton.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
//
//
class FragmentSetup(
    private val context: Context,
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val fragments: ArrayList<Fragment>,
    private val tabItems: Array<String>,
    private val tabIcons: TypedArray
) {
    fun setup() {
        val fragmentAdapter = MyFragmentStateAdapter(context as FragmentActivity, fragments)
        viewPager.adapter = fragmentAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabItems[position]
            tab.setIcon(tabIcons.getResourceId(position, -1))
            val iconColor = ContextCompat.getColor(context, R.color.text_icon_dark)
            tab.icon?.setTint(iconColor)
        }.attach()
    }
}

