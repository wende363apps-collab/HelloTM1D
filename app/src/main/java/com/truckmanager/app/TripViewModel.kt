package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: TripRepository

    init {
        val dao = AppDatabase.getDatabase(application).tripDao()
        repo = TripRepository(dao)
    }

    fun addTrip(destination: String, date: String, revenue: Double, expenses: Double) {
        viewModelScope.launch {
            val trip = Trip(destination = destination, date = date, revenue = revenue, expenses = expenses)
            repo.insertTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repo.deleteTrip(trip)
        }
    }
}
