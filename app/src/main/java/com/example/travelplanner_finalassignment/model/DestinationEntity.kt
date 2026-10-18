package com.example.travelplanner_finalassignment.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "destinations",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
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