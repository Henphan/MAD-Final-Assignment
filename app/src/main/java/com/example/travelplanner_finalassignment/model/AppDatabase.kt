package com.example.travelplanner_finalassignment.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Trip::class, Destination::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun destinationDao(): DestinationDao
    companion object {
        private var db: AppDatabase? = null
        fun get(context: Context): AppDatabase {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    klass = AppDatabase::class.java, "app.db"
                ).build()
            }
            return db!!
        }
    }
}
