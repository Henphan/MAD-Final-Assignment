package com.example.travelplanner_finalassignment.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Trip::class, Destination::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun destinationDao(): DestinationDao
    companion object {
        @Volatile
        private var db: AppDatabase? = null
        fun get(context: Context): AppDatabase {
            return db ?: synchronized(this) {
                db ?: Room.databaseBuilder(
                    context.applicationContext,
                    klass = AppDatabase::class.java, "app.db"
                ).build().also { db = it }
            }
        }
    }
}
