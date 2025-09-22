package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
                RootScreen()
            }
        }
    }
}

private enum class BottomTab(val title: String) {
    DASHBOARD("Dashboard"),
    TRIPS("Trips"),
    REPORTS("Reports")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootScreen() {
    var selectedTab by remember { mutableStateOf(BottomTab.TRIPS) } // default to Trips (your main screen)
    val title = selectedTab.title

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("TM1D • $title") }) },
        bottomBar = {
            NavigationBar {
                BottomTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        // We avoid icon libs to keep deps minimal.
                        // NavigationBarItem requires an icon slot, so we pass a tiny placeholder.
                        icon = { Box(modifier = Modifier.padding(6.dp)) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            BottomTab.DASHBOARD -> DashboardScreen(Modifier.padding(padding))
            BottomTab.TRIPS -> TripScreen(Modifier.padding(padding))
            BottomTab.REPORTS -> ReportsScreen(Modifier.padding(padding))
        }
    }
}

/* -------------------- DASHBOARD -------------------- */

@Composable
private fun DashboardScreen(modifier: Modifier = Modifier) {
    // Simple placeholder dashboard; you can expand later.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Welcome to TM1D 🚛", style = MaterialTheme.typography.headlineSmall)
        Text("Quick stats (demo):")
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Trips: 0")
                Text("Total Distance: 0 km")
            }
        }
    }
}

/* -------------------- TRIPS (Form + List) -------------------- */

@Composable
private fun TripScreen(
    modifier: Modifier = Modifier,
    viewModel: TripViewModel = viewModel()
) {
    val trips by viewModel.allTrips.observeAsState(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form state
        var name by remember { mutableStateOf("") }
        var destination by remember { mutableStateOf("") }
        var distanceText by remember { mutableStateOf("") }

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

/* -------------------- REPORTS (Placeholder) -------------------- */

@Composable
private fun ReportsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Reports", style = MaterialTheme.typography.headlineSmall)
        Text("Coming soon: filters, date ranges, PDF export…")
    }
}
