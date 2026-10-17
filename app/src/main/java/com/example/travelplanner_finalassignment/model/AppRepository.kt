package com.example.travelplanner_finalassignment.model

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlin.text.insert

class AppRepository (private val db: AppDatabase){
    private val trips = db.tripDao()
    private val destinations = db.destinationDao()

    fun lTrips(): Flow<List<Trip>> =
        trips.getAllTrips()
    fun getTrip(id: Int): Flow<Trip?> = trips.getTrip(id)
    fun observeForTrip(tripId: Int): Flow<List<Destination>> = destinations.observeForTrip(tripId)

    // trips related
    suspend fun createTrip(name: String, startDate: Long) =
        trips.insert(Trip(name = name, startDate = startDate))

    suspend fun updateTrip(trip: Trip) = trips.update(trip)

    suspend fun deleteTrip(trip: Trip) = trips.delete(trip)
}