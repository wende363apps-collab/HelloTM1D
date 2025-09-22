package com.truckmanager.app

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Application.dataStore by preferencesDataStore("settings")

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: TripRepository
    private val dao: TripDao

    // UI State
    val query = MutableLiveData("")
    val sort = MutableLiveData("date")
    private val pageSize = 20
    private val currentPage = MutableLiveData(0)

    // Paging data
    val tripsPaged: LiveData<List<Trip>> = MediatorLiveData<List<Trip>>().apply {
        val update: () -> Unit = {
            val q = query.value ?: ""
            val s = sort.value ?: "date"
            val p = currentPage.value ?: 0
            val src = repo.paged(q, s, pageSize, p * pageSize)
            addSource(src) { value = it }
        }
        addSource(query) { update() }
        addSource(sort) { update() }
        addSource(currentPage) { update() }
    }

    // Totals
    val totalIncome: LiveData<Double?>
    val totalCost: LiveData<Double?>

    // Theme
    private val KEY_DARK = booleanPreferencesKey("dark_theme")
    val isDarkTheme = liveData {
        val flow = getApplication<Application>().dataStore.data.map { it[KEY_DARK] ?: false }
        emit(flow.first())
        flow.asLiveData().let { addSource(it) { v -> emit(v) } }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>().dataStore.edit { it[KEY_DARK] = enabled }
        }
    }

    init {
        val db = AppDatabase.getDatabase(application)
        dao = db.tripDao()
        repo = TripRepository(dao)

        val (inc, cost) = repo.totals()
        totalIncome = inc
        totalCost = cost
    }

    fun loadFirstPage() { currentPage.value = 0 }
    fun loadNextPage() { currentPage.value = (currentPage.value ?: 0) + 1 }

    fun addTrip(
        name: String,
        destination: String,
        distance: Double,
        income: Double,
        cost: Double,
        date: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(Trip(name = name, destination = destination, distance = distance, income = income, cost = cost, date = date))
        loadFirstPage()
    }

    fun updateTrip(trip: Trip) = viewModelScope.launch(Dispatchers.IO) {
        repo.update(trip)
    }

    fun deleteTrip(trip: Trip) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(trip)
    }
}
