package com.truckmanager.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,             // Truck/Driver
    val destination: String,
    val distance: Double,         // km
    val income: Double,           // Birr
    val cost: Double,             // Birr
    val date: String              // simple ISO or free text
)
