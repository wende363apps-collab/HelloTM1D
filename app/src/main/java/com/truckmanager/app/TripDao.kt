package com.truckmanager.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Delete
    suspend fun deleteTrip(trip: Trip)
}
