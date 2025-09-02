package com.truckmanager.app

class TripRepository(private val tripDao: TripDao) {
    suspend fun insertTrip(trip: Trip) = tripDao.insertTrip(trip)
    suspend fun getAllTrips(): List<Trip> = tripDao.getAllTrips()
    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)
}
