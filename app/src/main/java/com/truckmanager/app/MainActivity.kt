package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
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
fun MainScreen(viewModel: TripViewModel = viewModel()) {
    // simple tab switching state (no nav lib needed)
    var selectedTab by remember { mutableStateOf(0) } // 0=Dashboard, 1=Trips
    val tabs = listOf("Dashboard", "Trips")

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (selectedTab == 0) "Dashboard" else "Trips") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {},
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel)
                1 -> TripsScreen(viewModel, snackbarHostState)
            }
        }
    }
}

/** DASHBOARD **/
@Composable
fun DashboardScreen(viewModel: TripViewModel) {
    val trips by viewModel.allTrips.observeAsState(emptyList())
    val totalTrips = trips.size
    val totalDistance = trips.sumOf { it.distance }
    val latest = trips.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Total Trips", totalTrips.toString())
        StatCard("Total Distance (km)", "%.2f".format(totalDistance))
        StatCard(
            "Latest Trip",
            if (latest != null) "${latest.name} → ${latest.destination}" else "—"
        )
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

/** TRIPS (Add + Search + Edit + Delete + Validation/Toasts) **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    viewModel: TripViewModel,
    snackbarHostState: SnackbarHostState
) {
    val trips by viewModel.filteredTrips.observeAsState(emptyList())

    var name by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }

    var search by remember { mutableStateOf("") }

    // edit dialog state
    var editing by remember { mutableStateOf<Trip?>(null) }
    var editName by remember { mutableStateOf("") }
    var editDestination by remember { mutableStateOf("") }
    var editDistance by remember { mutableStateOf("") }

    fun showSnack(msg: String) {
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search
        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                viewModel.setSearchQuery(it)
            },
            label = { Text("Search (name or destination)") },
            modifier = Modifier.fillMaxWidth()
        )

        Divider()

        // Add Trip form
        Text("Add Trip", style = MaterialTheme.typography.titleMedium)
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                val km = distance.toDoubleOrNull() ?: -1.0
                val ok = viewModel.addTrip(name, destination, km)
                if (ok) {
                    name = ""; destination = ""; distance = ""
                    showSnack("Trip added")
                } else {
                    showSnack("Please enter valid Name, Destination and Distance > 0")
                }
            }) {
                Text("Add Trip")
            }
        }

        Divider()

        // Trip list
        Text("Trips", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(trips, key = { it.id }) { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // open edit dialog
                            editing = trip
                            editName = trip.name
                            editDestination = trip.destination
                            editDistance = trip.distance.toString()
                        },
                    elevation = CardDefaults.cardElevation(1.dp)
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                editing = trip
                                editName = trip.name
                                editDestination = trip.destination
                                editDistance = trip.distance.toString()
                            }) { Text("Edit") }
                            TextButton(onClick = {
                                viewModel.deleteTrip(trip)
                                showSnack("Trip deleted")
                            }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }

    // Edit dialog
    if (editing != null) {
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Edit Trip") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Truck/Driver Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDestination,
                        onValueChange = { editDestination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDistance,
                        onValueChange = { editDistance = it },
                        label = { Text("Distance (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val base = editing!!
                    val km = editDistance.toDoubleOrNull() ?: -1.0
                    val updated = base.copy(
                        name = editName.trim(),
                        destination = editDestination.trim(),
                        distance = km
                    )
                    val ok = viewModel.updateTrip(updated)
                    if (ok) {
                        editing = null
                        showSnack("Trip updated")
                    } else {
                        showSnack("Please enter valid values (distance > 0)")
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editing = null }) { Text("Cancel") }
            }
        )
    }
}
