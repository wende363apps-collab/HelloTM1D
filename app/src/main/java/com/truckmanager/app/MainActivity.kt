package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
    val items = listOf("Dashboard", "Trips", "Expenses", "Settings")
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(item) },
                        icon = { /* Icons can be added later */ }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "Dashboard",
            Modifier.padding(innerPadding)
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
    Text("Dashboard 🚛", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun TripsScreen() {
    Text("Trips Page 🛣️", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun ExpensesScreen() {
    Text("Expenses Page 💸", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun SettingsScreen() {
    Text("Settings Page ⚙️", style = MaterialTheme.typography.headlineMedium)
}
