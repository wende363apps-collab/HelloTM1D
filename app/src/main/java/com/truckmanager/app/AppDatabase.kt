package com.truckmanager.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Voyage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun voyageDao(): VoyageDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tm1d_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
