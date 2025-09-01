package com.truckmanager.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voyages")
data class Voyage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val voyageNumber: Int,
    val departure: String,
    val arrival: String,
    val cargoPrice: Double,
    val expenses: Double,
    val netIncome: Double
)
