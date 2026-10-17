package com.example.travelplanner_finalassignment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tripId: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val nights: Int,
    val position: Int
)