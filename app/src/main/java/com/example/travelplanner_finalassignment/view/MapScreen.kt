package com.example.travelplanner_finalassignment.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.travelplanner_finalassignment.model.Destination
import com.example.travelplanner_finalassignment.model.googleMapsUrl
import com.example.travelplanner_finalassignment.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

private val routeColours = listOf(
    Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFB8C00), Color(0xFF8E24AA)
)
private val markerHues = listOf(
    BitmapDescriptorFactory.HUE_RED,
    BitmapDescriptorFactory.HUE_AZURE,
    BitmapDescriptorFactory.HUE_GREEN,
    BitmapDescriptorFactory.HUE_ORANGE,
    BitmapDescriptorFactory.HUE_VIOLET
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel
) {
    val context = LocalContext.current
    val tripsOnMap = viewModel.tripsOnMap
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 1f)
    }
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded, tripsOnMap) {
        val points = tripsOnMap.flatMap { it.destinations }.map { LatLng(it.latitude, it.longitude) }
        if (!mapLoaded || points.isEmpty()) return@LaunchedEffect
        if (points.size == 1) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(points.first(), 8f))
        } else {
            val bounds = LatLngBounds.builder()
            points.forEach { bounds.include(it) }
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewModel.selectedTripId == null,
                    onClick = { viewModel.selectTrip(null) },
                    label = { Text("All trips") }
                )
                viewModel.trips.forEach { trip ->
                    FilterChip(
                        selected = viewModel.selectedTripId == trip.id,
                        onClick = { viewModel.selectTrip(trip.id) },
                        label = { Text(trip.name) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = { mapLoaded = true }
                ) {
                    tripsOnMap.forEach { tripOnMap ->
                        val colourIndex = tripOnMap.colourIndex % routeColours.size

                        if (tripOnMap.destinations.size > 1) {
                            Polyline(
                                points = tripOnMap.destinations.map { LatLng(it.latitude, it.longitude) },
                                color = routeColours[colourIndex],
                                width = 8f
                            )
                        }

                        tripOnMap.destinations.forEachIndexed { index, destination ->
                            key(destination.id) {
                                Marker(
                                    state = rememberUpdatedMarkerState(
                                        position = LatLng(destination.latitude, destination.longitude)
                                    ),
                                    title = "${index + 1}. ${destination.name}",
                                    snippet = "${tripOnMap.trip.name} · tap to open in Google Maps",
                                    icon = BitmapDescriptorFactory.defaultMarker(markerHues[colourIndex]),
                                    onInfoWindowClick = { openInGoogleMaps(context, destination) }
                                )
                            }
                        }
                    }
                }

                if (tripsOnMap.isEmpty()) {
                    Card(modifier = Modifier.align(Alignment.Center).padding(24.dp)) {
                        Text(
                            "No cities to show yet. Add cities to a trip to see them on the map.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun openInGoogleMaps(context: Context, destination: Destination) {
    val intent = Intent(Intent.ACTION_VIEW, destination.googleMapsUrl().toUri())
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app found to open maps", Toast.LENGTH_SHORT).show()
    }
}
