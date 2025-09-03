package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TripRepository

    val trips: StateFlow<List<Trip>>

    init {
        val dao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(dao)
        trips = repository.getAllTrips()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun addTrip(origin: String, destination: String, date: String, cost: Double) {
        viewModelScope.launch {
            val trip = Trip(origin = origin, destination = destination, date = date, cost = cost)
            repository.insertTrip(trip)
        }
    }
}
