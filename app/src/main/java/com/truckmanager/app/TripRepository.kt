package com.truckmanager.app

class TripRepository(private val tripDao: TripDao) {
    val allTrips = tripDao.getAllTrips()

    suspend fun insert(trip: Trip) = tripDao.insertTrip(trip)
    suspend fun delete(trip: Trip) = tripDao.deleteTrip(trip)
}
