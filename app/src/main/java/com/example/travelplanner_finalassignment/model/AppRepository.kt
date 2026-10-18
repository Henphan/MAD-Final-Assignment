package com.example.travelplanner_finalassignment.model

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

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

    fun observeAllDestinations(): Flow<List<Destination>> = destinations.observeAll()

    suspend fun addDestination(
        tripId: Int,
        name: String,
        latitude: Double,
        longitude: Double,
        nights: Int
    ): Long = db.withTransaction {
        val position = destinations.getForTrip(tripId).size
        destinations.insert(
            Destination(
                tripId = tripId,
                name = name,
                latitude = latitude,
                longitude = longitude,
                nights = nights,
                position = position
            )
        )
    }

    suspend fun removeDestination(destination: Destination) = db.withTransaction {
        destinations.delete(destination)
        renumber(destinations.getForTrip(destination.tripId))
    }

    suspend fun updateDestinationNights(destination: Destination, nights: Int) =
        destinations.update(destination.copy(nights = nights.coerceIn(MIN_NIGHTS, MAX_NIGHTS)))

    suspend fun moveDestination(destination: Destination, delta: Int) = db.withTransaction {
        val route = destinations.getForTrip(destination.tripId).toMutableList()
        val from = route.indexOfFirst { it.id == destination.id }
        val to = (from + delta).coerceIn(0, route.lastIndex)
        if (from != -1 && from != to) {
            route.add(to, route.removeAt(from))
            renumber(route)
        }
    }

    private suspend fun renumber(route: List<Destination>) =
        destinations.update(route.mapIndexed { index, city -> city.copy(position = index) })
}