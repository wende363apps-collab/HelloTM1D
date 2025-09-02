package com.truckmanager.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val truckNumber: String,
    val origin: String,
    val destination: String,
    val revenue: Double,
    val expenses: Double,
    val date: String
)
