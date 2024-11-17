package com.example.hoophubskeleton.fragment

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    when (locationPermissionState.status) {
        is PermissionStatus.Granted -> {
            ShowMap()
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
