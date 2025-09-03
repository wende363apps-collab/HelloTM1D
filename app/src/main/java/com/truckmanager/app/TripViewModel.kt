package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TripRepository

    val allTrips by lazy {
        repository.allTrips.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    init {
        val dao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(dao)
    }

    fun addTrip(origin: String, destination: String, date: String, cost: Double, revenue: Double) {
        viewModelScope.launch {
            repository.insert(Trip(origin = origin, destination = destination, date = date, cost = cost, revenue = revenue))
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.delete(trip)
        }
    }
}
