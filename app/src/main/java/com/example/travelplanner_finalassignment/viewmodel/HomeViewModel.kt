package com.example.travelplanner_finalassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.model.AppRepository
import com.example.travelplanner_finalassignment.model.Trip
import com.example.travelplanner_finalassignment.model.isValidTripName
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AppRepository
) : ViewModel() {

    var trips by mutableStateOf(listOf<Trip>())
        private set

    init {
        viewModelScope.launch {
            repository.lTrips().collect { result -> trips = result }
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
}
