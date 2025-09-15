package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf("dashboard") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "dashboard",
                    onClick = { selectedTab = "dashboard" },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = selectedTab == "trips",
                    onClick = { selectedTab = "trips" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Trips") },
                    label = { Text("Trips") }
                )
                NavigationBarItem(
                    selected = selectedTab == "settings",
                    onClick = { selectedTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                "dashboard" -> DashboardScreen()
                "trips" -> TripScreen()
                "settings" -> SettingsScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    val revenue = 120000.0
    val expenses = 45000.0
    val netIncome = revenue - expenses

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatCard("Revenue", "$revenue Birr")
        StatCard("Expenses", "$expenses Birr")
        StatCard("Net Income", "$netIncome Birr")
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

@Composable
fun TripScreen(viewModel: TripViewModel = viewModel()) {
    val allTrips by viewModel.allTrips.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form
        var name by remember { mutableStateOf("") }
        var destination by remember { mutableStateOf("") }
        var distance by remember { mutableStateOf("") }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Truck/Driver Name") })
        OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") })
        OutlinedTextField(value = distance, onValueChange = { distance = it }, label = { Text("Distance (km)") })

        Button(
            onClick = {
                if (name.isNotBlank() && destination.isNotBlank() && distance.isNotBlank()) {
                    viewModel.addTrip(name, destination, distance.toDoubleOrNull() ?: 0.0)
                    name = ""
                    destination = ""
                    distance = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Trip")
        }

        Divider()

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allTrips) { trip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Name: ${trip.name}", style = MaterialTheme.typography.titleMedium)
                            Text("Destination: ${trip.destination}")
                            Text("Distance: ${trip.distance} km")
                        }
                        Button(onClick = { viewModel.deleteTrip(trip) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Settings coming soon ⚙️")
    }
}
