package com.truckmanager.app

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TripDao {

    @Query("""
        SELECT * FROM trips
        WHERE name LIKE '%' || :query || '%' OR destination LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN :sort = 'name' THEN name END ASC,
            CASE WHEN :sort = 'destination' THEN destination END ASC,
            CASE WHEN :sort = 'distance' THEN distance END ASC,
            CASE WHEN :sort = 'date' THEN date END DESC,
            id DESC
        LIMIT :limit OFFSET :offset
    """)
    fun searchPaged(query: String, sort: String, limit: Int, offset: Int): LiveData<List<Trip>>

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun getAll(): LiveData<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)

    @Query("SELECT SUM(income) FROM trips")
    fun totalIncome(): LiveData<Double?>

    @Query("SELECT SUM(cost) FROM trips")
    fun totalCost(): LiveData<Double?>
}
