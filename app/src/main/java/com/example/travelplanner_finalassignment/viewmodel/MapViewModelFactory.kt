package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelplanner_finalassignment.model.AppRepository

class MapViewModelFactory (
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(modelClass.isAssignableFrom(MapViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return MapViewModel(repository) as T
    }
}
