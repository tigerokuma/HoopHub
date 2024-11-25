package com.example.hoophubskeleton.fragment.TopMenu

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
fun MapWithCourtsScreen() {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    when (locationPermissionState.status) {
        is PermissionStatus.Granted -> {
            MapAndCourtsView()
        }
        is PermissionStatus.Denied -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Location permission is required to display the map.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun MapAndCourtsView() {
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
                    context = context, // Pass context to fetch the key dynamically
                    userLocation = location,
                    radius = 5000, // 5 km radius
                    onSuccess = { courtsList ->
                        courts.clear()
                        courts.addAll(courtsList.sortedBy { calculateDistance(location, LatLng(it.latitude, it.longitude)) })
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                    }
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Map Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.66f) // Map occupies 66% of the height
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true // Enables the "My Location" button
                ),
                properties = MapProperties(
                    isMyLocationEnabled = true // Enables showing the user's current location on the map
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
                        snippet = court.address
                    )
                }
            }
        }

        // Courts List Section
        CourtList(
            courts = courts.toList(),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f) // List occupies 34% of the height
        )
    }
}

@Composable
fun CourtList(courts: List<BasketballCourt>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(8.dp)
    ) {
        items(courts) { court ->
            CourtCard(court)
        }
    }
}

@Composable
fun CourtCard(court: BasketballCourt) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = court.name, style = MaterialTheme.typography.titleMedium)
                Text(text = court.address, style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${court.distance} km", style = MaterialTheme.typography.bodySmall)
                Text(text = "⭐ ${court.rating}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun calculateDistance(userLocation: LatLng, courtLocation: LatLng): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        userLocation.latitude,
        userLocation.longitude,
        courtLocation.latitude,
        courtLocation.longitude,
        results
    )
    return results[0] / 1000 // Convert meters to kilometers
}

/**
 * Fetch the user's current location using FusedLocationProviderClient.
 */
fun fetchUserLocation(context: Context, onLocationFetched: (LatLng?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationFetched(LatLng(location.latitude, location.longitude))
            } else {
                onLocationFetched(null)
            }
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
        onLocationFetched(null)
    }
}
