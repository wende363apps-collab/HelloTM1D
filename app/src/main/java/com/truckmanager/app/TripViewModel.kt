package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: TripRepository

    val allTrips: LiveData<List<Trip>>

    init {
        val dao = AppDatabase.getDatabase(application).tripDao()
        repo = TripRepository(dao)
        allTrips = repo.allTrips
    }

    fun addTrip(name: String, destination: String, distance: Double) {
        val trip = Trip(name = name, destination = destination, distance = distance)
        viewModelScope.launch { repo.insertTrip(trip) }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { repo.deleteTrip(trip) }
    }
}
