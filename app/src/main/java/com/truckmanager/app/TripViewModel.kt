package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    init {
        val tripDao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(tripDao)
    }

    val allTrips = repository.allTrips

    // Now accepts Trip object directly
    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            repository.insertTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
