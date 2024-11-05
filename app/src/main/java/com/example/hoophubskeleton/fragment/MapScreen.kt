package com.example.hoophubskeleton.fragment

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
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

    // Default to a location (e.g., San Francisco)
    val defaultLocation = LatLng(37.7749, -122.4194)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    )
}
