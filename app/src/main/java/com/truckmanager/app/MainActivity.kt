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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState // ✅ Fix import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TripScreen()
            }
        }
    }
}

@Composable
fun TripScreen(viewModel: TripViewModel = viewModel()) {
    val allTrips by viewModel.allTrips.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TM1D Trips 🚛") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trip form
            var name by remember { mutableStateOf("") }
            var destination by remember { mutableStateOf("") }
            var distance by remember { mutableStateOf("") }

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Truck/Driver Name") })
            OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") })
            OutlinedTextField(value = distance, onValueChange = { distance = it }, label = { Text("Distance (km)") })

            Button(
                onClick = {
                    if (name.isNotBlank() && destination.isNotBlank() && distance.isNotBlank()) {
                        val trip = Trip(
                            name = name,
                            destination = destination,
                            distance = distance.toDoubleOrNull() ?: 0.0
                        )
                        viewModel.addTrip(trip) // ✅ Fixed: pass Trip object
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

            // Trip list
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
}
