package com.truckmanager.app

import androidx.lifecycle.LiveData

class TripRepository(private val tripDao: TripDao) {
    val allTrips: LiveData<List<Trip>> = tripDao.getAllTrips()

    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }
}
