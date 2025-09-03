package com.truckmanager.app

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun getAllTrips(): LiveData<List<Trip>>
}
