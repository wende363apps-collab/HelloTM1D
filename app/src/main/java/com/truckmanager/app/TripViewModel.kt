package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    // Backing for search text
    private val searchQuery = MutableLiveData("")

    // Expose filtered trips without Transformations: switch sources manually
    val filteredTrips = MediatorLiveData<List<Trip>>()

    val allTrips: LiveData<List<Trip>>

    // Keep track of currently attached source so we can swap it
    private var currentSource: LiveData<List<Trip>>

    init {
        val dao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(dao)
        allTrips = repository.allTrips

        // Start with all trips
        currentSource = allTrips
        filteredTrips.addSource(currentSource) { filteredTrips.value = it }

        // When search changes, switch the source
        filteredTrips.addSource(searchQuery) { q ->
            // remove old source
            filteredTrips.removeSource(currentSource)
            currentSource = if (q.isNullOrBlank()) {
                allTrips
            } else {
                repository.searchTrips(q)
            }
            // attach new source
            filteredTrips.addSource(currentSource) { filteredTrips.value = it }
        }
    }

    fun setSearchQuery(q: String) {
        searchQuery.value = q
    }

    fun addTrip(name: String, destination: String, distanceKm: Double): Boolean {
        if (name.isBlank() || destination.isBlank() || distanceKm <= 0.0) return false
        viewModelScope.launch {
            repository.insertTrip(
                Trip(
                    name = name.trim(),
                    destination = destination.trim(),
                    distance = distanceKm
                )
            )
        }
        return true
    }

    fun updateTrip(trip: Trip): Boolean {
        if (trip.name.isBlank() || trip.destination.isBlank() || trip.distance <= 0.0) return false
        viewModelScope.launch { repository.updateTrip(trip) }
        return true
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { repository.deleteTrip(trip) }
    }
}
