package com.example.hoophubskeleton.fragment

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
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun ShowMap() {
    val context = LocalContext.current // Access context in @Composable
    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = true
            )
        )
    }
    var properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            )
        )
    }

    // Mutable state for user location
    val userLocation = remember { mutableStateOf<LatLng?>(null) }

    // Fetch user's location in a side effect
    LaunchedEffect(Unit) {
        fetchUserLocation(context) { location ->
            userLocation.value = location ?: LatLng(49.2827, -123.1207) // Default to Vancouver, BC
        }
    }

    // Default camera position centered on user location (or Vancouver)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value ?: LatLng(49.2827, -123.1207), 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        // Add a red marker at the user's location
        val location = userLocation.value ?: LatLng(49.2827, -123.1207) // Default to Vancouver
        Marker(
            state = MarkerState(position = location),
            title = "You are here",
            snippet = "Vancouver, BC"
        )
    }
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

    // Mock basketball courts
    val mockCourts = listOf(
        BasketballCourt("Downtown Court", "123 Brooklyn St", 4.7, 37.7789, -122.4194),
        BasketballCourt("Park Side Court", "456 Park Ave", 4.1, 37.7709, -122.4294),
        BasketballCourt("Bayview Court", "789 Mission St", 4.5, 37.7840, -122.4035)
    )

    LaunchedEffect(Unit) {
        fetchUserLocation(context) { location ->
            userLocation.value = location ?: LatLng(37.7749, -122.4194) // Default to SF
            if (location != null) {
                // Calculate distances and sort courts
                mockCourts.forEach { court ->
                    court.distance = calculateDistance(location, LatLng(court.latitude, court.longitude))
                }
                courts.clear()
                courts.addAll(mockCourts.sortedBy { it.distance }) // Sort by distance
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
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    userLocation.value ?: LatLng(37.7749, -122.4194), 12f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
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
                .fillMaxHeight(0.34f) // List occupies 34% of the height
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
