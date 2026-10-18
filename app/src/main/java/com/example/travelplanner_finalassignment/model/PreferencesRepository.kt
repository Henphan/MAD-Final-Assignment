package com.example.travelplanner_finalassignment.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class PreferencesRepository( // an interface for accessing preferences stored in DataStore
    private val context: Context
){
    private val DARK_MODE = booleanPreferencesKey("dark_mode") // creating the key to access the stored value for dark mode

    val isDarkMode = context.dataStore.data.map { preferences -> // isDarkMode is a Flow<Boolean> value
        preferences[DARK_MODE] ?: false // defaults to false at the start
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled // updates the value stored inside DataStore
        }
    }
}
