package com.example.travelplanner_finalassignment.model

import kotlin.math.abs

fun isValidTripName(name: String): Boolean = name.isNotBlank()

const val MILLIS_PER_DAY = 86_400_000L

fun millisToEpochDay(millis: Long): Long = millis / MILLIS_PER_DAY

fun epochDayToMillis(epochDay: Long): Long = epochDay * MILLIS_PER_DAY

const val MIN_NIGHTS = 0
const val MAX_NIGHTS = 60

fun isValidNights(nights: Int): Boolean = nights in MIN_NIGHTS..MAX_NIGHTS

fun countNights(destinations: List<Destination>): Int = destinations.sumOf { it.nights }

fun isValidLatitude(latitude: Double): Boolean = latitude in -90.0..90.0

fun isValidLongitude(longitude: Double): Boolean = longitude in -180.0..180.0

fun Destination.isSameCityAs(city: CityResult): Boolean =
    name.equals(city.name, ignoreCase = true) &&
        abs(latitude - city.latitude) < 0.01 &&
        abs(longitude - city.longitude) < 0.01

fun Destination.googleMapsUrl(): String =
    "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
