package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    init {
        val tripDao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(tripDao)
    }

    // Raw list
    val allTrips: LiveData<List<Trip>> = repository.allTrips

    // Dashboard derives
    val tripsCount: LiveData<Int> = allTrips.map { it.size }
    val totalDistance: LiveData<Double> = allTrips.map { list -> list.sumOf { it.distance } }
    val latestTrip: LiveData<Trip?> = allTrips.map { list -> list.maxByOrNull { it.id } }

    // Actions
    fun addTrip(name: String, destination: String, distance: Double) {
        val trip = Trip(name = name, destination = destination, distance = distance)
        viewModelScope.launch { repository.insertTrip(trip) }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { repository.deleteTrip(trip) }
    }
}
