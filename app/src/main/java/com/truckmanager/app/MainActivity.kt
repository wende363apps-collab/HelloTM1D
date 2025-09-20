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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
        topBar = { TopAppBar(title = { Text("TM1D Trips 🚛") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Add Trip Form ---
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
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number)
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
            ) { Text("Add Trip") }

            Divider()

            // --- Trip list with Edit/Delete ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allTrips, key = { it.id }) { trip ->
                    TripRow(
                        trip = trip,
                        onEdit = { updated -> viewModel.updateTrip(updated) },
                        onDelete = { viewModel.deleteTrip(trip) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TripRow(
    trip: Trip,
    onEdit: (Trip) -> Unit,
    onDelete: () -> Unit
) {
    var showEdit by remember { mutableStateOf(false) }

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
            Column(Modifier.weight(1f)) {
                Text("Name: ${trip.name}", style = MaterialTheme.typography.titleMedium)
                Text("Destination: ${trip.destination}")
                Text("Distance: ${trip.distance} km")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { showEdit = true }) { Text("Edit") }
                OutlinedButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }

    if (showEdit) {
        EditTripDialog(
            original = trip,
            onDismiss = { showEdit = false },
            onSave = { updated ->
                onEdit(updated)
                showEdit = false
            }
        )
    }
}

@Composable
private fun EditTripDialog(
    original: Trip,
    onDismiss: () -> Unit,
    onSave: (Trip) -> Unit
) {
    var name by remember(original.id) { mutableStateOf(original.name) }
    var destination by remember(original.id) { mutableStateOf(original.destination) }
    var distance by remember(original.id) { mutableStateOf(original.distance.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Trip") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val km = distance.toDoubleOrNull()
                if (name.isNotBlank() && destination.isNotBlank() && km != null) {
                    onSave(original.copy(name = name, destination = destination, distance = km))
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
