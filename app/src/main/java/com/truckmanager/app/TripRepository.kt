package com.truckmanager.app

import androidx.lifecycle.LiveData

class TripRepository(private val tripDao: TripDao) {

    val allTrips: LiveData<List<Trip>> = tripDao.getAllTrips()

    fun searchTrips(query: String): LiveData<List<Trip>> {
        val q = "%${query.trim()}%"
        return tripDao.searchTrips(q)
    }

    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }
}
