package com.example.travelplanner_finalassignment.data

import com.example.travelplanner_finalassignment.model.CityResult
import com.example.travelplanner_finalassignment.model.isValidLatitude
import com.example.travelplanner_finalassignment.model.isValidLongitude
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

object CitySearchService {

    private const val BASE_URL = "https://nominatim.openstreetmap.org/search"

    suspend fun searchCities(query: String): List<CityResult> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = URL("$BASE_URL?q=$encodedQuery&format=jsonv2&featureType=city&limit=10")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent", "TravelPlanner-COMP2008/1.0 (university project)")
        connection.setRequestProperty("Accept-Language", Locale.getDefault().toLanguageTag())
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Search failed with HTTP ${connection.responseCode}")
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parseCities(body)
        } finally {
            connection.disconnect()
        }
    }

    fun parseCities(json: String): List<CityResult> {
        val array = JSONArray(json)
        val cities = mutableListOf<CityResult>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue

            val latitude = item.optString("lat").toDoubleOrNull()
            val longitude = item.optString("lon").toDoubleOrNull()
            if (latitude == null || longitude == null) continue
            if (!isValidLatitude(latitude) || !isValidLongitude(longitude)) continue

            val displayName = item.optString("display_name")
            val name = item.optString("name").ifBlank { displayName.substringBefore(",") }.trim()
            if (name.isBlank()) continue
            val region = displayName.substringAfter(", ", "")

            val city = CityResult(name, region, latitude, longitude)
            val isDuplicate = cities.any { it.name == city.name && it.region == city.region }
            if (!isDuplicate) cities += city
        }
        return cities
    }
}
