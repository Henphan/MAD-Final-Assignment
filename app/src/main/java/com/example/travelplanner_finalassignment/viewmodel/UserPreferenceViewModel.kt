package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.model.PreferencesRepository
import kotlinx.coroutines.launch

class UserPreferenceViewModel (
    private val repository: PreferencesRepository
): ViewModel(){
    val isDarkMode = repository.isDarkMode

    fun onThemeChange(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }
}