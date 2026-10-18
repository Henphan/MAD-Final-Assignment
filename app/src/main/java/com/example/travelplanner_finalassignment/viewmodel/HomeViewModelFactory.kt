package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelplanner_finalassignment.model.AppRepository
import com.example.travelplanner_finalassignment.model.HomePreferencesRepository

class HomeViewModelFactory (
    private val repository: AppRepository,
    private val preferences: HomePreferencesRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(modelClass.isAssignableFrom(HomeViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repository, preferences) as T
    }
}
