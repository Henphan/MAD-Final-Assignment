package com.example.travelplanner_finalassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelplanner_finalassignment.data.CitySearchService
import com.example.travelplanner_finalassignment.data.TripRepository
import com.example.travelplanner_finalassignment.model.CityResult
import com.example.travelplanner_finalassignment.model.Destination
import com.example.travelplanner_finalassignment.model.TransportMode
import com.example.travelplanner_finalassignment.model.Trip
import com.example.travelplanner_finalassignment.model.countNights
import com.example.travelplanner_finalassignment.model.isSameCityAs
import com.example.travelplanner_finalassignment.model.isValidTripName
import com.example.travelplanner_finalassignment.model.isValidNights
import com.example.travelplanner_finalassignment.ui.TripRoute
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException

enum class SearchError { EMPTY_QUERY, NO_CONNECTION, BAD_RESPONSE }

sealed interface CitySearchState {
    data object Idle : CitySearchState
    data object Loading : CitySearchState
    data class Success(val cities: List<CityResult>) : CitySearchState
    data object Empty : CitySearchState
    data class Error(val reason: SearchError) : CitySearchState
}

enum class AddCityResult { ADDED, DUPLICATE, INVALID_NIGHTS }

class TripViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val tripId: Long = savedStateHandle.toRoute<TripRoute>().tripId

    var trip by mutableStateOf<Trip?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var destinations by mutableStateOf(listOf<Destination>())
        private set

    var searchState by mutableStateOf<CitySearchState>(CitySearchState.Idle)
        private set

    private var lastQuery = ""

    val totalNights: Int
        get() = countNights(destinations)

    init {
        viewModelScope.launch {
            TripRepository.observeTrip(tripId).collect { result ->
                trip = result
                isLoading = false
            }
        }
        viewModelScope.launch {
            TripRepository.observeDestinations(tripId).collect { result ->
                destinations = result
            }
        }
    }

    fun createTrip(name: String, startDate: Long, onCreated: (Long) -> Unit): Boolean {
        if (!isValidTripName(name)) return false
        viewModelScope.launch {
            val newTripId = TripRepository.createTrip(name.trim(), startDate)
            onCreated(newTripId)
        }
        return true
    }

    fun searchCities(query: String) {
        lastQuery = query.trim()
        if (lastQuery.isEmpty()) {
            searchState = CitySearchState.Error(SearchError.EMPTY_QUERY)
            return
        }
        searchState = CitySearchState.Loading
        viewModelScope.launch {
            searchState = try {
                val cities = CitySearchService.searchCities(lastQuery)
                if (cities.isEmpty()) CitySearchState.Empty else CitySearchState.Success(cities)
            } catch (e: IOException) {
                CitySearchState.Error(SearchError.NO_CONNECTION)
            } catch (e: JSONException) {
                CitySearchState.Error(SearchError.BAD_RESPONSE)
            }
        }
    }

    fun retrySearch() {
        searchCities(lastQuery)
    }

    fun clearSearch() {
        lastQuery = ""
        searchState = CitySearchState.Idle
    }

    fun addCity(city: CityResult, nights: Int): AddCityResult {
        if (!isValidNights(nights)) return AddCityResult.INVALID_NIGHTS
        if (destinations.any { it.isSameCityAs(city) }) return AddCityResult.DUPLICATE
        viewModelScope.launch {
            TripRepository.addDestination(
                tripId = tripId,
                name = city.name,
                latitude = city.latitude,
                longitude = city.longitude,
                nights = nights,
                transport = TransportMode.NONE
            )
        }
        return AddCityResult.ADDED
    }

    fun removeCity(destination: Destination) {
        viewModelScope.launch { TripRepository.removeDestination(destination) }
    }

    fun canChangeNights(nights: Int, change: Int): Boolean = isValidNights(nights + change)

    fun changeNights(destination: Destination, change: Int) {
        if (!canChangeNights(destination.nights, change)) return
        viewModelScope.launch {
            TripRepository.updateDestinationDuration(destination, destination.nights + change)
        }
    }

    fun moveCityUp(destination: Destination) {
        viewModelScope.launch { TripRepository.moveDestination(destination, -1) }
    }

    fun moveCityDown(destination: Destination) {
        viewModelScope.launch { TripRepository.moveDestination(destination, 1) }
    }
}
