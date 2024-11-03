# HoopHub
CMPT362 Final Project

MainActivity Overview

Top and Bottom Tab Layouts:

The top tab layout (topTabMenu) and bottom tab layout (bottomTabMenu) are initialized with menu items and icons, managed by the MenuIconCreator utility class.

Top Tab Menu:

Switches between the three fragments of "Players", "Courts" (map), and "Profile". All of these are hown in the main content area.

PlayersFragment

// TODO

MapFragment

// TODO

ProfileFragment

// TODO

Fragment Management:

The fragmentSetup function initializes the fragments, including PlayersFragment, MapFragment, and ProfileFragment, and configures the ViewPager2 with these fragments using the FragmentSetup helper class.
Fragment icons and labels are aligned with the tab menu items, providing a consistent UI experience.
Modular Setup Functions:

tabMenuSetup() initializes both the top and bottom tab layouts with items and icons from resources.
fragmentSetup() handles fragment initialization and associates them with the tabs in the ViewPager2, enabling users to navigate between sections.
