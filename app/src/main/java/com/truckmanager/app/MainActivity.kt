package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import kotlinx.coroutines.launch
import androidx.navigation.compose.*

/* -------------------- DATABASE -------------------- */

// Trip entity
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val origin: String,
    val destination: String,
    val cargo: String,
    val date: String
)

// DAO
@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    suspend fun getAllTrips(): List<Trip>

    @Insert
    suspend fun insertTrip(trip: Trip)
}

// Database
@Database(entities = [Trip::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}

/* -------------------- MAIN ACTIVITY -------------------- */

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "truck_manager_db"
        ).build()

        setContent {
            MaterialTheme {
                AppNavHost(db)
            }
        }
    }
}

/* -------------------- NAVIGATION -------------------- */

@Composable
fun AppNavHost(db: AppDatabase) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("trips") { TripListScreen(db, navController) }
        composable("addTrip") { AddTripScreen(db, navController) }
    }
}

/* -------------------- DASHBOARD -------------------- */

@Composable
fun DashboardScreen(navController: NavController) {
    val revenue = remember { mutableStateOf(120000.0) }
    val expenses = remember { mutableStateOf(45000.0) }
    val netIncome = revenue.value - expenses.value

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("TM1D Dashboard 🚛") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("trips") }) {
                Text("Trips")
            }
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
            StatCard("Revenue", "${revenue.value} Birr")
            StatCard("Expenses", "${expenses.value} Birr")
            StatCard("Net Income", "$netIncome Birr")
        }
    }
}

/* -------------------- TRIP LIST -------------------- */

@Composable
fun TripListScreen(db: AppDatabase, navController: NavController) {
    var trips by remember { mutableStateOf(listOf<Trip>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            trips = db.tripDao().getAllTrips()
        }
    }

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Trips") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addTrip") }) {
                Text("+")
            }
        }
    ) { padding ->
        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No trips yet. Add one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(trips) { trip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("From: ${trip.origin} → To: ${trip.destination}")
                            Text("Cargo: ${trip.cargo}")
                            Text("Date: ${trip.date}")
                        }
                    }
                }
            }
        }
    }
}

/* -------------------- ADD TRIP -------------------- */

@Composable
fun AddTripScreen(db: AppDatabase, navController: NavController) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Add Trip") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origin") })
            OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") })
            OutlinedTextField(value = cargo, onValueChange = { cargo = it }, label = { Text("Cargo") })
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })

            Button(
                onClick = {
                    scope.launch {
                        db.tripDao().insertTrip(
                            Trip(origin = origin, destination = destination, cargo = cargo, date = date)
                        )
                        navController.popBackStack() // Go back to trip list
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Trip")
            }
        }
    }
}

/* -------------------- REUSABLE CARD -------------------- */

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
