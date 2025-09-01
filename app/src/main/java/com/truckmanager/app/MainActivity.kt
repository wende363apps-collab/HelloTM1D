package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.*

// --------------------- STEP 2: DATA MODELS ---------------------

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val origin: String,
    val destination: String,
    val distanceKm: Double,
    val revenue: Double,
    val expense: Double
)

@Entity
data class Truck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plateNumber: String,
    val model: String,
    val capacityTons: Double
)

@Entity
data class Driver(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val licenseNumber: String,
    val phone: String
)

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val amount: Double,
    val date: String
)

// --------------------- STEP 3: DAO ---------------------

@Dao
interface TripDao {
    @Insert suspend fun insert(trip: Trip)
    @Query("SELECT * FROM Trip") suspend fun getAll(): List<Trip>
}

@Dao
interface TruckDao {
    @Insert suspend fun insert(truck: Truck)
    @Query("SELECT * FROM Truck") suspend fun getAll(): List<Truck>
}

@Dao
interface DriverDao {
    @Insert suspend fun insert(driver: Driver)
    @Query("SELECT * FROM Driver") suspend fun getAll(): List<Driver>
}

@Dao
interface ExpenseDao {
    @Insert suspend fun insert(expense: Expense)
    @Query("SELECT * FROM Expense") suspend fun getAll(): List<Expense>
}

// --------------------- STEP 4: DATABASE ---------------------

@Database(entities = [Trip::class, Truck::class, Driver::class, Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun truckDao(): TruckDao
    abstract fun driverDao(): DriverDao
    abstract fun expenseDao(): ExpenseDao
}

// --------------------- STEP 1 & 5: UI + NAVIGATION ---------------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckManagerApp()
        }
    }
}

@Composable
fun TruckManagerApp() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Trips") },
                    label = { Text("Trips") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Trucks") },
                    label = { Text("Trucks") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Drivers") },
                    label = { Text("Drivers") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Expenses") },
                    label = { Text("Expenses") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> DashboardScreen()
                1 -> TripsScreen()
                2 -> TrucksScreen()
                3 -> DriversScreen()
                4 -> ExpensesScreen()
            }
        }
    }
}

@Composable fun DashboardScreen() {
    Text("📊 Dashboard Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun TripsScreen() {
    Text("🛣 Trips Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun TrucksScreen() {
    Text("🚛 Trucks Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun DriversScreen() {
    Text("👷 Drivers Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun ExpensesScreen() {
    Text("💰 Expenses Screen", style = MaterialTheme.typography.headlineMedium)
}
