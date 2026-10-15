package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelplanner_finalassignment.model.PreferencesRepository

class UserPreferenceViewModelFactory (
    private val repository: PreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(modelClass.isAssignableFrom(UserPreferenceViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return UserPreferenceViewModel(repository) as T
    }
}