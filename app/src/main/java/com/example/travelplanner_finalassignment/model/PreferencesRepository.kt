package com.example.travelplanner_finalassignment.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class PreferencesRepository(
    private val context: Context
){
    private val DARK_MODE = booleanPreferencesKey("dark_mode")

    val isDarkMode = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
}
