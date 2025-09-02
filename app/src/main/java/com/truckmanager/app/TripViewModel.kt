package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val tripDao = AppDatabase.getDatabase(application).tripDao()
    private val repository: TripRepository = TripRepository(tripDao)

    val allTrips: StateFlow<List<Trip>> =
        repository.allTrips.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun addTrip(name: String, distance: Int) {
        viewModelScope.launch {
            repository.insert(Trip(name = name, distance = distance))
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.delete(trip)
        }
    }
}
