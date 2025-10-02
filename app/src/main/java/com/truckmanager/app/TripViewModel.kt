package com.truckmanager.app

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    // Backing for search text
    private val searchQuery = MutableLiveData<String>("")

    // Expose filtered trips: when search is blank -> allTrips; otherwise -> searchTrips
    val filteredTrips: LiveData<List<Trip>>

    val allTrips: LiveData<List<Trip>>

    init {
        val dao = AppDatabase.getDatabase(application).tripDao()
        repository = TripRepository(dao)
        allTrips = repository.allTrips

        filteredTrips = Transformations.switchMap(searchQuery) { q ->
            if (q.isBlank()) {
                allTrips
            } else {
                repository.searchTrips(q)
            }
        }
    }

    fun setSearchQuery(q: String) {
        searchQuery.value = q
    }

    fun addTrip(name: String, destination: String, distanceKm: Double): Boolean {
        // basic validation
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
