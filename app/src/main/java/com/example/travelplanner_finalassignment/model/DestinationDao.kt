package com.example.travelplanner_finalassignment.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destinations WHERE tripId = :tripId ORDER BY position")
    fun observeForTrip(tripId: Int): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE tripId = :tripId ORDER BY position")
    suspend fun getForTrip(tripId: Int): List<Destination>

    @Insert
    suspend fun insert(destination: Destination): Long

    @Update
    suspend fun update(destinations: List<Destination>)

    @Delete
    suspend fun delete(destination: Destination)
}