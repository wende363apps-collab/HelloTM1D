package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Room once here and pass into Compose
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "truckmanager-db"
        ).build()

        setContent {
            TruckDashboard(db)
        }
    }
}

@Composable
fun TruckDashboard(db: AppDatabase) {
    val scope = rememberCoroutineScope()

    // Live trips from DB (Flow -> collected into Compose state)
    var trips by remember { mutableStateOf(listOf<Trip>()) }

    LaunchedEffect(Unit) {
        // Collect the Flow continuously
        db.tripDao().getAllTrips().collect { list ->
            trips = list
        }
    }

    val totalRevenue = remember(trips) { trips.sumOf { it.revenue } }
    val totalExpenses = remember(trips) { trips.sumOf { it.expenses } }
    val netIncome = remember(trips) { totalRevenue - totalExpenses }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Insert a sample trip to verify DB writes
                    scope.launch {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val today = sdf.format(Date())
                        db.tripDao().insertTrip(
                            Trip(
                                truckNumber = "TM-1001",
                                origin = "Addis Ababa",
                                destination = "Djibouti",
                                revenue = 120000.0,
                                expenses = 45000.0,
                                date = today
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add sample trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "TM1D Dashboard 🚛",
                style = MaterialTheme.typography.headlineSmall
            )

            // Summary cards
            StatCard("Revenue", "Birr %.2f".format(totalRevenue))
            StatCard("Expenses", "Birr %.2f".format(totalExpenses))
            StatCard("Net Income", "Birr %.2f".format(netIncome))

            // Trips list
            Text(
                text = "Recent Trips",
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trips) { trip ->
                    TripCard(trip)
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
            .height(90.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Truck: ${trip.truckNumber}", style = MaterialTheme.typography.titleMedium)
            Text("${trip.origin} → ${trip.destination}")
            Text("Revenue: Birr %.2f | Expenses: Birr %.2f".format(trip.revenue, trip.expenses))
            Text("Date: ${trip.date}")
        }
    }
}
