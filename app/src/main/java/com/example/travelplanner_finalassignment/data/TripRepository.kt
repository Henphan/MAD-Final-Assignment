package com.example.travelplanner_finalassignment.data

import com.example.travelplanner_finalassignment.model.Destination
import com.example.travelplanner_finalassignment.model.TransportMode
import com.example.travelplanner_finalassignment.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

object TripRepository {

    private val trips = MutableStateFlow(
        listOf(
            Trip(id = 1, name = "Japan", startDate = 20788),
            Trip(id = 2, name = "Italy", startDate = 20887),
            Trip(id = 3, name = "Portugal", startDate = 20974)
        )
    )

    private val destinations = MutableStateFlow(
        listOf(
            Destination(id = 1, tripId = 1, name = "Tokyo", latitude = 35.6812, longitude = 139.7671, nights = 4, position = 0),
            Destination(id = 2, tripId = 1, name = "Kyoto", latitude = 35.0116, longitude = 135.7681, nights = 3, position = 1, arrivalTransport = TransportMode.TRAIN),
            Destination(id = 3, tripId = 1, name = "Osaka", latitude = 34.6937, longitude = 135.5023, nights = 2, position = 2, arrivalTransport = TransportMode.TRAIN),
            Destination(id = 4, tripId = 2, name = "Rome", latitude = 41.9028, longitude = 12.4964, nights = 3, position = 0),
            Destination(id = 5, tripId = 2, name = "Florence", latitude = 43.7696, longitude = 11.2558, nights = 2, position = 1, arrivalTransport = TransportMode.TRAIN),
            Destination(id = 6, tripId = 2, name = "Venice", latitude = 45.4408, longitude = 12.3155, nights = 2, position = 2, arrivalTransport = TransportMode.TRAIN)
        )
    )

    private var nextTripId = 100L
    private var nextDestinationId = 100L

    fun observeTrips(): Flow<List<Trip>> = trips

    fun observeTrip(id: Long): Flow<Trip?> =
        trips.map { list -> list.find { it.id == id } }

    fun observeDestinations(tripId: Long): Flow<List<Destination>> =
        destinations.map { list -> list.filter { it.tripId == tripId }.sortedBy { it.position } }

    fun observeAllDestinations(): Flow<List<Destination>> =
        destinations.map { list -> list.sortedWith(compareBy({ it.tripId }, { it.position })) }

    suspend fun createTrip(name: String, startDate: Long): Long {
        val newTrip = Trip(id = nextTripId++, name = name, startDate = startDate)
        trips.value = trips.value + newTrip
        return newTrip.id
    }

    suspend fun addDestination(
        tripId: Long,
        name: String,
        latitude: Double,
        longitude: Double,
        nights: Int,
        transport: TransportMode
    ) {
        val position = destinations.value.count { it.tripId == tripId }
        val newDestination = Destination(
            id = nextDestinationId++,
            tripId = tripId,
            name = name,
            latitude = latitude,
            longitude = longitude,
            nights = nights,
            position = position,
            arrivalTransport = if (position == 0) TransportMode.NONE else transport
        )
        destinations.value = destinations.value + newDestination
    }

    suspend fun removeDestination(destination: Destination) {
        destinations.value = destinations.value.filter { it.id != destination.id }
        renumberDestinations(destination.tripId)
    }

    suspend fun updateDestinationDuration(destination: Destination, nights: Int) {
        replace(destination.copy(nights = nights.coerceIn(0, 60)))
    }

    suspend fun moveDestination(destination: Destination, delta: Int) {
        val route = destinations.value
            .filter { it.tripId == destination.tripId }
            .sortedBy { it.position }
            .toMutableList()
        val from = route.indexOfFirst { it.id == destination.id }
        val to = (from + delta).coerceIn(0, route.lastIndex)
        if (from == -1 || from == to) return
        route.add(to, route.removeAt(from))
        route.forEachIndexed { index, city -> replace(city.copy(position = index)) }
    }

    private fun renumberDestinations(tripId: Long) {
        val route = destinations.value.filter { it.tripId == tripId }.sortedBy { it.position }
        route.forEachIndexed { index, city ->
            val transport = if (index == 0) TransportMode.NONE else city.arrivalTransport
            replace(city.copy(position = index, arrivalTransport = transport))
        }
    }

    private fun replace(updated: Destination) {
        destinations.value = destinations.value.map { if (it.id == updated.id) updated else it }
    }
}
