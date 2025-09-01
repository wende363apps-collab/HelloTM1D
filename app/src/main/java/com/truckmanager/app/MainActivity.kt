package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Money
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf("dashboard") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, selectedItem) { selectedItem = it }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") { DashboardScreen() }
            composable("trips") { TripsScreen() }
            composable("expenses") { ExpensesScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == "dashboard",
            onClick = {
                navController.navigate("dashboard")
                onItemSelected("dashboard")
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = selectedItem == "trips",
            onClick = {
                navController.navigate("trips")
                onItemSelected("trips")
            },
            icon = { Icon(Icons.Default.List, contentDescription = "Trips") },
            label = { Text("Trips") }
        )
        NavigationBarItem(
            selected = selectedItem == "expenses",
            onClick = {
                navController.navigate("expenses")
                onItemSelected("expenses")
            },
            icon = { Icon(Icons.Default.Money, contentDescription = "Expenses") },
            label = { Text("Expenses") }
        )
    }
}

@Composable
fun DashboardScreen() {
    val revenue = remember { mutableStateOf(120000.0) }
    val expenses = remember { mutableStateOf(45000.0) }
    val netIncome = revenue.value - expenses.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatCard("Revenue", "${revenue.value} Birr")
        StatCard("Expenses", "${expenses.value} Birr")
        StatCard("Net Income", "$netIncome Birr")
    }
}

@Composable
fun TripsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Trips will be listed here 🚚", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ExpensesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Expenses will be tracked here 💰", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp)
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
