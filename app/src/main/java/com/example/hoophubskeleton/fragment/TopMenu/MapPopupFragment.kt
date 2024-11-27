package com.example.hoophubskeleton.fragment.TopMenu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.example.hoophubskeleton.data.BasketballCourt

@Composable
fun MapPopup(onCourtSelected: (BasketballCourt) -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        MapAndCourtsView(
            onCourtSelected = { court ->
                onCourtSelected(court) // Pass selected court to the parent callback
            }
        )
        Button(
            onClick = { onDismiss() },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Text("Close")
        }
    }
}

