package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelplanner_finalassignment.model.AppRepository

class TripViewModelFactory (
    private val repository: AppRepository,
    private val tripId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(modelClass.isAssignableFrom(TripViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return TripViewModel(repository, tripId) as T
    }
}
