package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository
    val allTrips: LiveData<List<Trip>>

    init {
        val tripDao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(tripDao)
        allTrips = repository.allTrips
    }

    fun addTrip(name: String, destination: String, distance: Double) {
        val trip = Trip(
            name = name,
            destination = destination,
            distance = distance
        )
        viewModelScope.launch { repository.insertTrip(trip) }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch { repository.updateTrip(trip) }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { repository.deleteTrip(trip) }
    }
}
