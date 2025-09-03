package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") { DashboardScreen(navController) }
                    composable("tripList") { TripListScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(navController: NavHostController) {
    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("TM1D Dashboard 🚛") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Trips") },
                onClick = { navController.navigate("tripList") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to TM1D!", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Manage your trucking business with ease.")
        }
    }
}

@Composable
fun TripListScreen(navController: NavHostController, viewModel: TripViewModel = viewModel()) {
    val trips by viewModel.trips.collectAsState()

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("All Trips") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add trip screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(trips) { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${trip.origin} → ${trip.destination}", style = MaterialTheme.typography.titleMedium)
                        Text("Date: ${trip.date}", style = MaterialTheme.typography.bodyMedium)
                        Text("Cost: ${trip.cost} Birr", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
