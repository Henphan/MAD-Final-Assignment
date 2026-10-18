package com.example.travelplanner_finalassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner_finalassignment.model.AppRepository
import com.example.travelplanner_finalassignment.model.HomePreferencesRepository
import com.example.travelplanner_finalassignment.model.Trip
import com.example.travelplanner_finalassignment.model.TripSort
import com.example.travelplanner_finalassignment.model.epochDayToMillis
import com.example.travelplanner_finalassignment.model.isValidTripName
import com.example.travelplanner_finalassignment.model.millisToEpochDay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TripRow(
    val id: Int,
    val name: String,
    val startLabel: String,
    val countdown: String,
    val shareText: String,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val trips: List<TripRow> = emptyList(),
    val sort: TripSort = TripSort.START_DATE,
)

class HomeViewModel(
    private val repository: AppRepository,
    private val preferences: HomePreferencesRepository,
    private val todayEpochDay: () -> Long = { millisToEpochDay(System.currentTimeMillis()) },
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        combine(repository.lTrips(), preferences.tripSort) { trips, sort ->
            HomeUiState(
                isLoading = false,
                trips = sortTrips(trips, sort).map { it.toRow() },
                sort = sort,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun onSortSelected(sort: TripSort) {
        viewModelScope.launch { preferences.setTripSort(sort) }
    }

    fun createTrip(name: String, startDate: Long, onCreated: (Int) -> Unit): Boolean {
        if (!isValidTripName(name)) return false
        viewModelScope.launch {
            val newTripId = repository.createTrip(name.trim(), startDate).toInt()
            onCreated(newTripId)
        }
        return true
    }

    fun deleteTrip(tripId: Int) {
        viewModelScope.launch {
            val trip = repository.getTrip(tripId).first() ?: return@launch
            repository.deleteTrip(trip)
        }
    }

    private fun Trip.toRow(): TripRow {
        val startLabel = formatEpochDay(startDate)
        return TripRow(
            id = id,
            name = name,
            startLabel = "Starts $startLabel",
            countdown = countdownLabel(startDate, todayEpochDay()),
            shareText = "I'm planning \"$name\", starting $startLabel.",
        )
    }
}

internal fun sortTrips(trips: List<Trip>, sort: TripSort): List<Trip> = when (sort) {
    TripSort.START_DATE -> trips.sortedBy { it.startDate }
    TripSort.NAME -> trips.sortedBy { it.name.lowercase() }
    TripSort.NEWEST -> trips.sortedByDescending { it.createdAt }
}

internal fun countdownLabel(startEpochDay: Long, todayEpochDay: Long): String {
    val days = startEpochDay - todayEpochDay
    return when {
        days > 1 -> "In $days days"
        days == 1L -> "Tomorrow"
        days == 0L -> "Starts today"
        days == -1L -> "Started yesterday"
        else -> "Started ${-days} days ago"
    }
}

private fun formatEpochDay(epochDay: Long): String {
    val format = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date(epochDayToMillis(epochDay)))
}
