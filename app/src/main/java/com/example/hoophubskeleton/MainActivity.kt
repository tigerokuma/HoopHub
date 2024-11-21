package com.example.hoophubskeleton

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.hoophubskeleton.R.*
import com.example.hoophubskeleton.fragment.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.hoophubskeleton.adapter.MyFragmentStateAdapter
import com.example.hoophubskeleton.fragment.BottomMenu.BookingsFragment
import com.example.hoophubskeleton.fragment.BottomMenu.ExploreFragment
import com.example.hoophubskeleton.fragment.BottomMenu.SavedFragment
import com.example.hoophubskeleton.fragment.BottomMenu.SettingsFragment
import com.example.hoophubskeleton.fragment.TopMenu.MapFragment
import com.example.hoophubskeleton.fragment.TopMenu.PlayersFragment
import com.example.hoophubskeleton.fragment.TopMenu.ProfileFragment
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {
    private lateinit var topTabMenu: TabLayout
    private lateinit var bottomTabMenu: TabLayout
    private lateinit var topViewPager: ViewPager2
    private lateinit var bottomViewPager: ViewPager2
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        Util.checkPermissions(this) // Check user permissions

        // Set up NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize menus
        setupTopTabs()
        setupBottomTabs()

        // Attach NavController to Top and Bottom Tabs
        setupNavigationWithTabs()
    }

    private fun setupTopTabs() {
        topTabMenu = findViewById(R.id.top_tab_menu)
        topViewPager = findViewById(R.id.top_view_pager)

        val topMenuItems = resources.getStringArray(R.array.top_menu_items)
        val topMenuIcons = resources.obtainTypedArray(R.array.top_menu_icons)

        val topFragments = arrayListOf(
            PlayersFragment(),
            MapFragment(),
            ProfileFragment()
        )

        FragmentSetup(
            this,
            topTabMenu,
            topViewPager,
            topFragments,
            topMenuItems,
            topMenuIcons
        ).setup()
    }

    private fun setupBottomTabs() {
        bottomTabMenu = findViewById(R.id.bottom_tab_menu)
        bottomViewPager = findViewById(R.id.bottom_view_pager)

        val bottomMenuItems = resources.getStringArray(R.array.bottom_menu_items)
        val bottomMenuIcons = resources.obtainTypedArray(R.array.bottom_menu_icons)

        val bottomFragments = arrayListOf(
            InboxFragment(),
            BookingsFragment(),
            SavedFragment(),
            ExploreFragment(),
            SettingsFragment()
        )

        FragmentSetup(
            this,
            bottomTabMenu,
            bottomViewPager,
            bottomFragments,
            bottomMenuItems,
            bottomMenuIcons
        ).setup()
    }

    private fun setupNavigationWithTabs() {
        // Example: Navigate to InboxFragment from top or bottom tabs
        topTabMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> navController.navigate(R.id.action_global_to_inboxFragment) // Example action
                    1 -> navController.navigate(R.id.action_global_to_mapFragment)
                    2 -> navController.navigate(R.id.action_global_to_profileFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        bottomTabMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> navController.navigate(R.id.action_global_to_inboxFragment)
                    1 -> navController.navigate(R.id.action_global_to_bookingsFragment)
                    2 -> navController.navigate(R.id.action_global_to_savedFragment)
                    3 -> navController.navigate(R.id.action_global_to_exploreFragment)
                    4 -> navController.navigate(R.id.action_global_to_settingsFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}
