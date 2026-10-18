package com.example.travelplanner_finalassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.data.CitySearchService
import com.example.travelplanner_finalassignment.model.AppRepository
import com.example.travelplanner_finalassignment.model.CityResult
import com.example.travelplanner_finalassignment.model.Destination
import com.example.travelplanner_finalassignment.model.Trip
import com.example.travelplanner_finalassignment.model.countNights
import com.example.travelplanner_finalassignment.model.isSameCityAs
import com.example.travelplanner_finalassignment.model.isValidTripName
import com.example.travelplanner_finalassignment.model.isValidNights
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

class TripViewModel(
    private val repository: AppRepository,
    private val tripId: Int
) : ViewModel() {

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
            repository.getTrip(tripId).collect { result ->
                trip = result
                isLoading = false
            }
        }
        viewModelScope.launch {
            repository.observeForTrip(tripId).collect { result ->
                destinations = result
            }
        }
    }

    fun createTrip(name: String, startDate: Long, onCreated: (Int) -> Unit): Boolean {
        if (!isValidTripName(name)) return false
        viewModelScope.launch {
            val newTripId = repository.createTrip(name.trim(), startDate).toInt()
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
            repository.addDestination(
                tripId = tripId,
                name = city.name,
                latitude = city.latitude,
                longitude = city.longitude,
                nights = nights
            )
        }
        return AddCityResult.ADDED
    }

    fun removeCity(destination: Destination) {
        viewModelScope.launch { repository.removeDestination(destination) }
    }

    fun canChangeNights(nights: Int, change: Int): Boolean = isValidNights(nights + change)

    fun changeNights(destination: Destination, change: Int) {
        if (!canChangeNights(destination.nights, change)) return
        viewModelScope.launch {
            repository.updateDestinationNights(destination, destination.nights + change)
        }
    }

    fun moveCityUp(destination: Destination) {
        viewModelScope.launch { repository.moveDestination(destination, -1) }
    }

    fun moveCityDown(destination: Destination) {
        viewModelScope.launch { repository.moveDestination(destination, 1) }
    }
}
