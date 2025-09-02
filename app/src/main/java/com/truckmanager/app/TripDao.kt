package com.truckmanager.app

import androidx.room.*

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Query("SELECT * FROM trips ORDER BY date DESC")
    suspend fun getAllTrips(): List<Trip>

    @Delete
    suspend fun deleteTrip(trip: Trip)
}
