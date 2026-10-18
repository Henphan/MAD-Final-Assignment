package com.example.travelplanner_finalassignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelplanner_finalassignment.model.Destination
import com.example.travelplanner_finalassignment.viewmodel.AddCityResult
import com.example.travelplanner_finalassignment.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    onBack: () -> Unit,
    onOpenTrip: (Long) -> Unit,
    vm: TripViewModel = viewModel()
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showNewTripDialog by rememberSaveable { mutableStateOf(false) }
    val trip = vm.trip

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(trip?.name ?: "Trip", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showNewTripDialog = true }) { Text("New trip") }
                }
            )
        },
        floatingActionButton = {
            if (trip != null) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Add city") }
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        when {
            vm.isLoading -> Box(contentModifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            trip == null -> Box(contentModifier.padding(16.dp), contentAlignment = Alignment.Center) {
                Text("This trip could not be found.")
            }
            else -> CityList(
                destinations = vm.destinations,
                totalNights = vm.totalNights,
                canChangeNights = vm::canChangeNights,
                onChangeNights = vm::changeNights,
                onMoveUp = vm::moveCityUp,
                onMoveDown = vm::moveCityDown,
                onRemove = vm::removeCity,
                modifier = contentModifier
            )
        }
    }

    if (showAddDialog) {
        AddCityDialog(
            searchState = vm.searchState,
            onSearch = vm::searchCities,
            onRetry = vm::retrySearch,
            canChangeNights = vm::canChangeNights,
            onAddCity = { city, nights ->
                val result = vm.addCity(city, nights)
                if (result == AddCityResult.ADDED) {
                    showAddDialog = false
                    vm.clearSearch()
                }
                result
            },
            onDismiss = {
                showAddDialog = false
                vm.clearSearch()
            }
        )
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

@Composable
private fun CityList(
    destinations: List<Destination>,
    totalNights: Int,
    canChangeNights: (Int, Int) -> Boolean,
    onChangeNights: (Destination, Int) -> Unit,
    onMoveUp: (Destination) -> Unit,
    onMoveDown: (Destination) -> Unit,
    onRemove: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val cities = if (destinations.size == 1) "1 city" else "${destinations.size} cities"
            val nights = if (totalNights == 1) "1 night" else "$totalNights nights"
            Text("$cities · $nights", style = MaterialTheme.typography.titleMedium)
        }
        if (destinations.isEmpty()) {
            item {
                Text(
                    "No cities yet. Tap Add city to start planning your route.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        itemsIndexed(destinations, key = { _, destination -> destination.id }) { index, destination ->
            CityCard(
                number = index + 1,
                destination = destination,
                isFirst = index == 0,
                isLast = index == destinations.lastIndex,
                canDecrease = canChangeNights(destination.nights, -1),
                canIncrease = canChangeNights(destination.nights, 1),
                onDecrease = { onChangeNights(destination, -1) },
                onIncrease = { onChangeNights(destination, 1) },
                onMoveUp = { onMoveUp(destination) },
                onMoveDown = { onMoveDown(destination) },
                onRemove = { onRemove(destination) }
            )
        }
    }
}

@Composable
private fun CityCard(
    number: Int,
    destination: Destination,
    isFirst: Boolean,
    isLast: Boolean,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$number. ${destination.name}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onMoveUp, enabled = !isFirst) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move ${destination.name} earlier")
                }
                IconButton(onClick = onMoveDown, enabled = !isLast) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move ${destination.name} later")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove ${destination.name}")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nights", modifier = Modifier.weight(1f))
                NightsStepper(
                    nights = destination.nights,
                    canDecrease = canDecrease,
                    canIncrease = canIncrease,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease
                )
            }
        }
    }
}

@Composable
fun NightsStepper(
    nights: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onDecrease, enabled = canDecrease) {
            Icon(Icons.Filled.Remove, contentDescription = "One less night")
        }
        Text(if (nights == 1) "1 night" else "$nights nights")
        IconButton(onClick = onIncrease, enabled = canIncrease) {
            Icon(Icons.Filled.Add, contentDescription = "One more night")
        }
    }
}
