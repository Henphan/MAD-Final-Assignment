package com.example.travelplanner_finalassignment.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.travelplanner_finalassignment.model.TripSort
import com.example.travelplanner_finalassignment.viewmodel.HomeViewModel
import com.example.travelplanner_finalassignment.viewmodel.TripRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenTrip: (Int) -> Unit,
    onOpenMap: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendingDeleteId by rememberSaveable { mutableStateOf<Int?>(null) }
    var showNewTripDialog by rememberSaveable { mutableStateOf(false) }
    val onAddTrip = { showNewTripDialog = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { WaypointTitle() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    IconButton(onClick = onOpenMap) {
                        Icon(Icons.Filled.Map, contentDescription = "Show all trips on a map")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewTripDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add trip")
            }
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                "My trips",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            )
            SortChips(selected = state.sort, onSelect = viewModel::onSortSelected)

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.trips.isEmpty() -> EmptyState(onAddTrip = onAddTrip)
                else -> TripGrid(
                    trips = state.trips,
                    onOpenTrip = onOpenTrip,
                    onShareTrip = { trip -> shareTrip(context, trip.shareText) },
                    onDeleteTrip = { trip -> pendingDeleteId = trip.id },
                )
            }
        }
    }

    if (showNewTripDialog) {
        AddTripDialog(
            onCreate = { name, startDate ->
                val accepted = viewModel.createTrip(name, startDate, onCreated = onOpenTrip)
                if (accepted) showNewTripDialog = false
                accepted
            },
            onDismiss = { showNewTripDialog = false },
        )
    }

    val tripToDelete = state.trips.firstOrNull { it.id == pendingDeleteId }
    if (tripToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete ${tripToDelete.name}?") },
            text = { Text("This trip and everything planned in it will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTrip(tripToDelete.id)
                    pendingDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun SortChips(selected: TripSort, onSelect: (TripSort) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TripSort.entries.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option.label) },
            )
        }
    }
}

@Composable
private fun TripGrid(
    trips: List<TripRow>,
    onOpenTrip: (Int) -> Unit,
    onShareTrip: (TripRow) -> Unit,
    onDeleteTrip: (TripRow) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(trips, key = { it.id }) { trip ->
            TripCard(
                trip = trip,
                onOpen = { onOpenTrip(trip.id) },
                onShare = { onShareTrip(trip) },
                onDelete = { onDeleteTrip(trip) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripCard(trip: TripRow, onOpen: () -> Unit, onShare: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(trip.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    trip.startLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    trip.countdown,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onShare) {
                Icon(Icons.Filled.Share, contentDescription = "Share ${trip.name}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete ${trip.name}")
            }
        }
    }
}

@Composable
private fun EmptyState(onAddTrip: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("No trips yet", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap + to plan your first trip.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAddTrip) { Text("Plan a trip") }
    }
}

private fun shareTrip(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    try {
        context.startActivity(Intent.createChooser(send, "Share trip"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app available to share with", Toast.LENGTH_SHORT).show()
    }
}
