package com.example.hoophubskeleton.fragment.TopMenu

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hoophubskeleton.data.BasketballCourt
import com.example.hoophubskeleton.fetchNearbyBasketballCourts
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapWithMarkersForSelection(
    onCourtSelected: (BasketballCourt) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val userLocation = remember { mutableStateOf<LatLng?>(null) }
    val courts = remember { mutableStateListOf<BasketballCourt>() }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(37.7749, -122.4194), 12f // Default to SF
        )
    }

    LaunchedEffect(Unit) {
        fetchUserLocation(context) { location ->
            userLocation.value = location ?: LatLng(37.7749, -122.4194) // Default to SF
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                userLocation.value ?: LatLng(37.7749, -122.4194), 12f
            )

            if (location != null) {
                fetchNearbyBasketballCourts(
                    context = context,
                    userLocation = location,
                    radius = 5000,
                    onSuccess = { courtsList ->
                        courts.clear()
                        courts.addAll(courtsList)
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                    }
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Card around the map for styling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp) // Add spacing around the map
                .height(400.dp), // Adjust height as needed
            shape = MaterialTheme.shapes.medium // Rounded corners
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true
                ),
                properties = MapProperties(
                    isMyLocationEnabled = true
                )
            ) {
                userLocation.value?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "You are here",
                        snippet = "Your location"
                    )
                }

                courts.forEach { court ->
                    Marker(
                        state = MarkerState(position = LatLng(court.latitude, court.longitude)),
                        title = court.name,
                        snippet = court.address,
                        onClick = {
                            onCourtSelected(court)
                            true
                        }
                    )
                }
            }
        }

        // Close Button
        Button(
            onClick = { onDismiss() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Close")
        }
    }
}
