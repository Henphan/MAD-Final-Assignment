package com.example.travelplanner_finalassignment.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.travelplanner_finalassignment.model.CityResult
import com.example.travelplanner_finalassignment.viewmodel.AddCityResult
import com.example.travelplanner_finalassignment.viewmodel.CitySearchState
import com.example.travelplanner_finalassignment.viewmodel.SearchError

@Composable
fun AddCityDialog(
    searchState: CitySearchState,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    canChangeNights: (Int, Int) -> Boolean,
    onAddCity: (CityResult, Int) -> AddCityResult,
    onDismiss: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var nights by rememberSaveable { mutableIntStateOf(2) }
    var addResult by rememberSaveable { mutableStateOf<AddCityResult?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a city") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        addResult = null
                    },
                    label = { Text("City name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                    trailingIcon = {
                        IconButton(onClick = { onSearch(query) }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Nights", modifier = Modifier.weight(1f))
                    NightsStepper(
                        nights = nights,
                        canDecrease = canChangeNights(nights, -1),
                        canIncrease = canChangeNights(nights, 1),
                        onDecrease = { nights-- },
                        onIncrease = { nights++ }
                    )
                }

                SearchResults(
                    searchState = searchState,
                    onRetry = onRetry,
                    onCityClick = { city -> addResult = onAddCity(city, nights) }
                )

                if (addResult == AddCityResult.DUPLICATE) {
                    Text("That city is already in this trip.", color = MaterialTheme.colorScheme.error)
                }

                Text(
                    "City search © OpenStreetMap contributors",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SearchResults(
    searchState: CitySearchState,
    onRetry: () -> Unit,
    onCityClick: (CityResult) -> Unit
) {
    when (searchState) {
        CitySearchState.Idle -> Text(
            "Search for a city, then tap it to add it to this trip.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CitySearchState.Loading -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
            Text("Searching…")
        }

        CitySearchState.Empty -> Text("No cities found. Check the spelling or try another name.")

        is CitySearchState.Error -> Column {
            val message = when (searchState.reason) {
                SearchError.EMPTY_QUERY -> "Type a city name first."
                SearchError.NO_CONNECTION -> "Couldn't reach the search service. Check your internet connection."
                SearchError.BAD_RESPONSE -> "The search service sent back something we couldn't read."
            }
            Text(message, color = MaterialTheme.colorScheme.error)
            if (searchState.reason != SearchError.EMPTY_QUERY) {
                TextButton(onClick = onRetry) { Text("Retry") }
            }
        }

        is CitySearchState.Success -> Column {
            searchState.cities.forEach { city ->
                ListItem(
                    headlineContent = { Text(city.name) },
                    supportingContent = {
                        Text(city.region, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable { onCityClick(city) }
                )
                HorizontalDivider()
            }
        }
    }
}
