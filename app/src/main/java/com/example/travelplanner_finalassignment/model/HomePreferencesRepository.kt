package com.example.travelplanner_finalassignment.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class TripSort(val label: String) {
    START_DATE("Date"),
    NAME("Name"),
    NEWEST("Newest"),
}

private val Context.homeDataStore by preferencesDataStore(name = "home_preferences")

class HomePreferencesRepository(private val context: Context) {

    private val tripSortKey = stringPreferencesKey("trip_sort")

    val tripSort: Flow<TripSort> = context.homeDataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val saved = prefs[tripSortKey]
            TripSort.entries.firstOrNull { it.name == saved } ?: TripSort.START_DATE
        }

    suspend fun setTripSort(sort: TripSort) {
        context.homeDataStore.edit { prefs -> prefs[tripSortKey] = sort.name }
    }
}
