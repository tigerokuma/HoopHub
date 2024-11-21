package com.example.hoophubskeleton

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout

object MenuIconCreator {
    // Creates menu icons for the top and bottom tabs of the main page

    class MenuIcon(context: Context) : LinearLayout(context) {
        // Each tab is a menu icon
        init {
            LayoutInflater.from(context).inflate(R.layout.generic_menu_item, this, true)
        }

        fun setIcon(resId: Int) {
            findViewById<ImageView>(R.id.tab_icon).setImageResource(resId)
        }

        fun setText(text: String) {
            findViewById<TextView>(R.id.tab_text).text = text
        }
    }

    fun addTabs(tabLayout: TabLayout, tabNames: Array<String>, tabIcons: List<Int>, context: Context) {
        // Adds a menu icon to whatever TabLayout is passed to it
        for (i in tabNames.indices) {
            val tab = tabLayout.newTab()
            tab.customView = createTabView(tabNames[i], tabIcons[i], context)
            tabLayout.addTab(tab)
        }
    }

    private fun createTabView(title: String, iconResId: Int, context: Context): View {
        // Creates and returns a menu icon to add to a tab
        val menuIcon = MenuIcon(context)
        menuIcon.setIcon(iconResId)
        menuIcon.setText(title)
        return menuIcon
    }

    fun addTabsWithListener(
        tabLayout: TabLayout,
        tabNames: Array<String>,
        tabIcons: List<Int>,
        context: Context,
        onTabSelected: (Int) -> Unit
    ) {
        for (i in tabNames.indices) {
            val tab = tabLayout.newTab()
            tab.customView = createTabView(tabNames[i], tabIcons[i], context)
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { onTabSelected(it.position) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
