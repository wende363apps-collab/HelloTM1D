package com.truckmanager.app

import androidx.room.*

@Dao
interface VoyageDao {
    @Insert
    suspend fun insertVoyage(voyage: Voyage)

    @Query("SELECT * FROM voyages ORDER BY voyageNumber DESC")
    suspend fun getAllVoyages(): List<Voyage>
}
