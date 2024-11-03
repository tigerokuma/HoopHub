package com.example.hoophubskeleton

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.hoophubskeleton.R.*
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var topTabMenu: TabLayout
    private lateinit var mainContent: ViewPager2
    private lateinit var bottomTabMenu: TabLayout
    private lateinit var fragmentSetup: FragmentSetup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)
        // Checking user permissions
        Util.checkPermissions(this)
        // Setting up tabs and fragments
        tabMenuSetup()
        fragmentSetup()


    }

    private fun tabMenuSetup() {
        topTabMenu = findViewById(id.top_tab_menu)
        topTabMenu.setSelectedTabIndicatorColor(resources.getColor(R.color.highlight))
        mainContent = findViewById(id.main_content)
        bottomTabMenu = findViewById(id.bottom_tab_menu)
        val topMenuItems = resources.getStringArray(array.top_menu_items)
        val topMenuIcons = resources.obtainTypedArray(array.top_menu_icons)
        val bottomMenuItems = resources.getStringArray(array.bottom_menu_items)
        val bottomMenuIcons = resources.obtainTypedArray(array.bottom_menu_icons)

        MenuIconCreator.addTabs(topTabMenu, topMenuItems, topMenuIcons, this)
        MenuIconCreator.addTabs(bottomTabMenu, bottomMenuItems, bottomMenuIcons, this)

    }

    private fun fragmentSetup() {
        // list of fragments
        val fragments = arrayListOf<Fragment>(
            PlayersFragment(),
            MapFragment(),
            ProfileFragment()
        )

        // Get the top menu items and icons
        val topMenuItems = resources.getStringArray(array.top_menu_items)
        val topMenuIcons = resources.obtainTypedArray(array.top_menu_icons)

        // Initialize FragmentSetupHelper and set up fragments
        fragmentSetup = FragmentSetup(this, topTabMenu, mainContent, topMenuItems, topMenuIcons)
        fragmentSetup.setupFragments(fragments)
    }
}