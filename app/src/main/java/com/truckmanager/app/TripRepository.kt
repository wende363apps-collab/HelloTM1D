package com.truckmanager.app

import kotlinx.coroutines.flow.Flow

class TripRepository(private val dao: TripDao) {
    fun getAllTrips(): Flow<List<Trip>> = dao.getAllTrips()
    suspend fun insertTrip(trip: Trip) = dao.insertTrip(trip)
}
