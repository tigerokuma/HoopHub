package com.example.hoophubskeleton.fragment

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

class MapFragment : Fragment() {
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                MapWithCourtsScreen() // Updated Composable
            }
        }
    }
}
