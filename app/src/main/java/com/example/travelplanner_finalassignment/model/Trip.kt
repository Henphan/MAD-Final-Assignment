package com.example.travelplanner_finalassignment.model

data class Trip(
    val id: Long = 0,
    val name: String,
    val startDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransportMode { NONE, WALK, CAR, BUS, TRAIN, FERRY, FLIGHT }

data class Destination(
    val id: Long = 0,
    val tripId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val nights: Int,
    val position: Int,
    val arrivalTransport: TransportMode = TransportMode.NONE
)
