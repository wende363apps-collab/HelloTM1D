package com.truckmanager.app

import androidx.room.Database
import androidx.room.RoomDatabase

// Example entity (replace with your real data classes later)
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val origin: String,
    val destination: String,
    val distanceKm: Int
)

// DAO (Data Access Object) for trips
@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getAllTrips(): List<Trip>

    @Insert
    fun insertTrip(trip: Trip)
}

// Database class
@Database(entities = [Trip::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
