package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.model.PreferencesRepository
import kotlinx.coroutines.launch

class UserPreferenceViewModel (
    private val repository: PreferencesRepository
): ViewModel(){
    val isDarkMode = repository.isDarkMode // a Flow<Boolean> obtained from repository

    fun onThemeChange(enabled: Boolean) { // runs when slider changes, requests to update the theme to the currently selected one
        viewModelScope.launch { // starts a co-routine
            repository.setDarkMode(enabled) // setDarkMode is a suspend function, must run in coroutine
        }
    }
}