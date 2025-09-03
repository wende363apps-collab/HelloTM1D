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

    // LiveData for all trips
    val allTrips = repository.allTrips

    // Add trip using individual parameters (matches MainActivity)
    fun addTrip(name: String, destination: String, distance: Double) {
        val trip = Trip(
            name = name,
            destination = destination,
            distance = distance
        )
        viewModelScope.launch {
            repository.insertTrip(trip)
        }
    }

    // Delete trip
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
