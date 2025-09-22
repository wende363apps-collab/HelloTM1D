package com.truckmanager.app

import androidx.lifecycle.LiveData

class TripRepository(private val dao: TripDao) {

    fun paged(query: String, sort: String, limit: Int, offset: Int): LiveData<List<Trip>> =
        dao.searchPaged(query, sort, limit, offset)

    fun all(): LiveData<List<Trip>> = dao.getAll()

    fun totals(): Pair<LiveData<Double?>, LiveData<Double?>> = dao.totalIncome() to dao.totalCost()

    suspend fun insert(trip: Trip) = dao.insert(trip)
    suspend fun update(trip: Trip) = dao.update(trip)
    suspend fun delete(trip: Trip) = dao.delete(trip)
}
