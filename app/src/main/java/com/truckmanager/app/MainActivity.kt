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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class) // ✅ Fix for SmallTopAppBar
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
fun TripScreen(viewModel: TripViewModel = viewModel()) {
    var destination by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var revenue by remember { mutableStateOf("") }
    var expenses by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("TM1D - Trips 🚛") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = revenue,
                onValueChange = { revenue = it },
                label = { Text("Revenue (Birr)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = expenses,
                onValueChange = { expenses = it },
                label = { Text("Expenses (Birr)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (destination.isNotBlank() && date.isNotBlank() &&
                        revenue.isNotBlank() && expenses.isNotBlank()
                    ) {
                        viewModel.addTrip(
                            destination,
                            date,
                            revenue.toDouble(),
                            expenses.toDouble()
                        )
                        destination = ""
                        date = ""
                        revenue = ""
                        expenses = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Trip")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Trips List", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 🚧 Step 6 will connect this to DB
                items(listOf<Trip>()) { trip ->
                    TripItem(trip)
                }
            }
        }
    }
}

@Composable
fun TripItem(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Destination: ${trip.destination}")
            Text("Date: ${trip.date}")
            Text("Revenue: ${trip.revenue} Birr")
            Text("Expenses: ${trip.expenses} Birr")
        }
    }
}
