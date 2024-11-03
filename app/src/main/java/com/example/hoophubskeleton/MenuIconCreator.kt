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
    // creates menu icons for the top and bottom tabs of the main page

    class MenuIcon(context: Context) : LinearLayout(context) {
        // each tab is a menu icon
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

    fun addTabs(tabLayout: TabLayout, tabNames: Array<String>, tabIcons: TypedArray, context: Context) {
        // adds a menu icon to whatever tablayout is passed to it
        for (i in tabNames.indices) {
            val tab = tabLayout.newTab()
            tab.customView = createTabView(tabNames[i], tabIcons.getResourceId(i, -1), context)
            tabLayout.addTab(tab)
        }
        tabIcons.recycle()
    }

    private fun createTabView(title: String, iconResId: Int, context: Context): View {
        // creates and returns a menu icon to add to a tab
        val menuIcon = MenuIcon(context)
        menuIcon.setIcon(iconResId)
        menuIcon.setText(title)
        return menuIcon
    }
}
