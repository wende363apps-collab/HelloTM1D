package com.truckmanager.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Trip::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
