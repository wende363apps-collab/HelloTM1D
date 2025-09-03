package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val tripViewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TripsScreen(tripViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(viewModel: TripViewModel) {
    val trips by viewModel.allTrips.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("Trips 🚛") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (trips.isEmpty()) {
                Text("No trips yet. Add your first one!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trips) { trip ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("${trip.origin} → ${trip.destination}")
                                Text("Date: ${trip.date}")
                                Text("Cost: ${trip.cost} Birr")
                                Text("Revenue: ${trip.revenue} Birr")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddTripDialog(
            onDismiss = { showDialog = false },
            onAdd = { origin, dest, date, cost, rev ->
                viewModel.addTrip(origin, dest, date, cost, rev)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddTripDialog(onDismiss: () -> Unit, onAdd: (String, String, String, Double, Double) -> Unit) {
    var origin by remember { mutableStateOf("") }
    var dest by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var revenue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (origin.isNotBlank() && dest.isNotBlank()) {
                    onAdd(origin, dest, date, cost.toDoubleOrNull() ?: 0.0, revenue.toDoubleOrNull() ?: 0.0)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add New Trip") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origin") })
                OutlinedTextField(value = dest, onValueChange = { dest = it }, label = { Text("Destination") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Cost") })
                OutlinedTextField(value = revenue, onValueChange = { revenue = it }, label = { Text("Revenue") })
            }
        }
    )
}
