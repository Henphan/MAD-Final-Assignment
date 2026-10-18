package com.example.travelplanner_finalassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.data.TripRepository
import com.example.travelplanner_finalassignment.model.Destination
import com.example.travelplanner_finalassignment.model.Trip
import kotlinx.coroutines.launch

data class TripOnMap(
    val trip: Trip,
    val destinations: List<Destination>,
    val colourIndex: Int
)

class MapViewModel : ViewModel() {

    var trips by mutableStateOf(listOf<Trip>())
        private set

    private var allDestinations by mutableStateOf(listOf<Destination>())

    var selectedTripId by mutableStateOf<Long?>(null)
        private set

    val tripsOnMap: List<TripOnMap>
        get() = trips
            .mapIndexed { index, trip ->
                TripOnMap(
                    trip = trip,
                    destinations = allDestinations.filter { it.tripId == trip.id },
                    colourIndex = index
                )
            }
            .filter { selectedTripId == null || it.trip.id == selectedTripId }
            .filter { it.destinations.isNotEmpty() }

    init {
        viewModelScope.launch {
            TripRepository.observeTrips().collect { result ->
                trips = result
                if (result.none { it.id == selectedTripId }) selectedTripId = null
            }
        }
        viewModelScope.launch {
            TripRepository.observeAllDestinations().collect { result ->
                allDestinations = result
            }
        }
    }

    fun selectTrip(tripId: Long?) {
        selectedTripId = if (trips.any { it.id == tripId }) tripId else null
    }
}
