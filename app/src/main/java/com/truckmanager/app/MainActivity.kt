package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
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
                TripScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(viewModel: TripViewModel = viewModel()) {
    val trips by viewModel.allTrips.observeAsState(emptyList())

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("TM1D Trips 🚛") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Form state
            var name by remember { mutableStateOf("") }
            var destination by remember { mutableStateOf("") }
            var distanceText by remember { mutableStateOf("") }

            // --- Form
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Truck/Driver Name") }
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Destination") }
            )
            OutlinedTextField(
                value = distanceText,
                onValueChange = { distanceText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Distance (km)") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    val km = distanceText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && destination.isNotBlank()) {
                        viewModel.addTrip(name, destination, km)
                        name = ""
                        destination = ""
                        distanceText = ""
                    }
                }) {
                    Text("Add Trip")
                }
            }

            Divider()

            // --- List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trips) { trip ->
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
