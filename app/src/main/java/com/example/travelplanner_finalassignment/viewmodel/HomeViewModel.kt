package com.example.travelplanner_finalassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.data.TripRepository
import com.example.travelplanner_finalassignment.model.Trip
import com.example.travelplanner_finalassignment.model.isValidTripName
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var trips by mutableStateOf(listOf<Trip>())
        private set

    init {
        viewModelScope.launch {
            TripRepository.observeTrips().collect { result -> trips = result }
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
}
