package com.example.hoophubskeleton.fragment.TopMenu

import android.Manifest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.hoophubskeleton.R
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

    // Location permission state
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Check and request permissions
    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }

    // Handle UI based on permissions
    when {
        locationPermissionState.allPermissionsGranted -> {
            // Permissions are granted; fetch user location
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

            // Display map and courts
            Box(modifier = Modifier.fillMaxSize()) {
                // Border for the map
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp) // Add spacing around the map
                        .height(410.dp) // Slightly larger to include the border
                        .border(
                            width = 4.dp,
                            color = colorResource(id = R.color.highlight), // Use @color/highlight
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    // Card containing the map
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp), // Spacing between the map and the border
                        shape = MaterialTheme.shapes.medium
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
                                    icon = getScaledBitmapDescriptor(context, R.drawable.pin_basketball, 150, 150), // Custom marker
                                    onClick = {
                                        onCourtSelected(court)
                                        true
                                    }
                                )
                            }
                        }
                    }
                }

                // Close Button
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.highlight) // Set the background color
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                ) {
                    Text("Close")
                }
            }
        }
        locationPermissionState.shouldShowRationale -> {
            // Show rationale
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Location permission is required to use this feature.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { locationPermissionState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
        else -> {
            // Show error or alternative UI
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Location permission denied. Enable it from app settings.")
            }
        }
    }
}
