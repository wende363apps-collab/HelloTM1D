package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete


class MainActivity : ComponentActivity() {
    private val tripViewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TripScreen(tripViewModel)
            }
        }
    }
}

@Composable
fun TripScreen(viewModel: TripViewModel) {
    val trips by viewModel.allTrips.collectAsState()

    var tripName by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("🚛 Trip Manager") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = tripName,
                onValueChange = { tripName = it },
                label = { Text("Trip Name") },
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
                    if (tripName.isNotBlank() && distance.isNotBlank()) {
                        viewModel.addTrip(tripName, distance.toIntOrNull() ?: 0)
                        tripName = ""
                        distance = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Trip")
            }

            Divider()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(trips) { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${trip.name} - ${trip.distance} km")
                            IconButton(onClick = { viewModel.deleteTrip(trip) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Trip"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
