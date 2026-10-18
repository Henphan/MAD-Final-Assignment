package com.example.travelplanner_finalassignment.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelplanner_finalassignment.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenTrip: (Long) -> Unit,
    onOpenMap: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    var showNewTripDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewTripDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add trip")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("My trips") },
                actions = {
                    IconButton(onClick = onOpenMap) {
                        Icon(Icons.Filled.Map, contentDescription = "Show all trips on a map")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Placeholder home screen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(vm.trips, key = { it.id }) { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTrip(trip.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(trip.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    if (showNewTripDialog) {
        AddTripDialog(
            onCreate = { name, startDate ->
                val accepted = vm.createTrip(name, startDate, onCreated = onOpenTrip)
                if (accepted) showNewTripDialog = false
                accepted
            },
            onDismiss = { showNewTripDialog = false }
        )
    }
}
