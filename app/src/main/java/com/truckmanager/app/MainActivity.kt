package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Room database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "truckmanager-db"
        ).build()

        // Insert a sample trip only once
        CoroutineScope(Dispatchers.IO).launch {
            val tripDao = db.tripDao()
            if (tripDao.getAllTrips().isEmpty()) {
                tripDao.insertTrip(
                    Trip(
                        truckNumber = "TM-1001",
                        origin = "Addis Ababa",
                        destination = "Djibouti",
                        revenue = 120000.0,
                        expenses = 45000.0
                    )
                )
            }
        }

        setContent {
            MaterialTheme {
                DashboardScreen(db)
            }
        }
    }
}

@Composable
fun DashboardScreen(db: AppDatabase) {
    var trips by remember { mutableStateOf(listOf<Trip>()) }

    // Load trips from DB
    LaunchedEffect(Unit) {
        trips = db.tripDao().getAllTrips()
    }

    val totalRevenue = trips.sumOf { it.revenue }
    val totalExpenses = trips.sumOf { it.expenses }
    val netIncome = totalRevenue - totalExpenses

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("TM1D Dashboard 🚛") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Summary cards
            StatCard("Revenue", "$totalRevenue Birr")
            StatCard("Expenses", "$totalExpenses Birr")
            StatCard("Net Income", "$netIncome Birr")

            // Show trips list
            trips.forEach { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Truck: ${trip.truckNumber}", style = MaterialTheme.typography.titleMedium)
                        Text("${trip.origin} ➝ ${trip.destination}")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
