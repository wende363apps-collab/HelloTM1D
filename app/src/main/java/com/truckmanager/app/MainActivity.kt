package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                val items = listOf(
                    BottomItem("dashboard", "Dashboard"),
                    BottomItem("trips", "Trips")
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val destination = nav.currentBackStackEntryAsState().value?.destination?.route
                            items.forEach { item ->
                                NavigationBarItem(
                                    selected = destination == item.route,
                                    onClick = {
                                        nav.navigate(item.route) {
                                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    label = { Text(item.label) },
                                    icon = { /* simple labels, no icons for now */ }
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = nav,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("dashboard") { DashboardScreen() }
                        composable("trips") { TripsScreen() }
                    }
                }
            }
        }
    }
}

data class BottomItem(val route: String, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: TripViewModel = viewModel()) {
    val count by viewModel.tripsCount.observeAsState(0)
    val total by viewModel.totalDistance.observeAsState(0.0)
    val latest by viewModel.latestTrip.observeAsState(null)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Dashboard") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard("Total Trips", count.toString())
            StatCard("Total Distance (km)", "%.2f".format(total))
            StatCard(
                "Latest Trip",
                if (latest != null) "${latest!!.name} → ${latest!!.destination}"
                else "No trips yet"
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(2.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(viewModel: TripViewModel = viewModel()) {
    val allTrips by viewModel.allTrips.observeAsState(emptyList())

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Trips") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Form
            var name by remember { mutableStateOf("") }
            var destination by remember { mutableStateOf("") }
            var distance by remember { mutableStateOf("") }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Truck/Driver Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance (km)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val km = distance.toDoubleOrNull()
                    if (name.isNotBlank() && destination.isNotBlank() && km != null) {
                        viewModel.addTrip(name, destination, km)
                        name = ""; destination = ""; distance = ""
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Name: ${trip.name}", style = MaterialTheme.typography.titleMedium)
                                Text("Destination: ${trip.destination}")
                                Text("Distance: ${trip.distance} km")
                            }
                            TextButton(onClick = { viewModel.deleteTrip(trip) }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
