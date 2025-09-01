package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    val tabs = listOf(
        "Dashboard",
        "Trips",
        "Expenses",
        "Settings"
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(title) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(title) },
                        icon = { /* add icons later */ }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Dashboard") { DashboardScreen() }
            composable("Trips") { TripsScreen() }
            composable("Expenses") { ExpensesScreen() }
            composable("Settings") { SettingsScreen() }
        }
    }
}

@Composable
fun DashboardScreen() {
    var voyages by remember { mutableStateOf(listOf<Voyage>()) }
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.voyageDao()

    LaunchedEffect(Unit) {
        voyages = dao.getAllVoyages()
    }

    Column {
        Button(onClick = {
            val nextNumber = (voyages.firstOrNull()?.voyageNumber ?: 0) + 1
            val newVoyage = Voyage(
                voyageNumber = nextNumber,
                departure = "City A",
                arrival = "City B",
                cargoPrice = 5000.0,
                expenses = 2000.0,
                netIncome = 3000.0
            )
            LaunchedEffect(Unit) {
                dao.insertVoyage(newVoyage)
                voyages = dao.getAllVoyages()
            }
        }) {
            Text("Add Voyage")
        }

        voyages.forEach { v ->
            Text("Voyage ${v.voyageNumber}: Net = ${v.netIncome} Birr")
        }
    }
}


@Composable
fun TripsScreen() {
    Text("Trips 🛣️", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun ExpensesScreen() {
    Text("Expenses 💸", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun SettingsScreen() {
    Text("Settings ⚙️", style = MaterialTheme.typography.headlineMedium)
}
