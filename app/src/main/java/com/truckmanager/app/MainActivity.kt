package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckManagerApp()
        }
    }
}

@Composable
fun TruckManagerApp() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Trips") },
                    label = { Text("Trips") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Trucks") },
                    label = { Text("Trucks") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Drivers") },
                    label = { Text("Drivers") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Expenses") },
                    label = { Text("Expenses") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> DashboardScreen()
                1 -> TripsScreen()
                2 -> TrucksScreen()
                3 -> DriversScreen()
                4 -> ExpensesScreen()
            }
        }
    }
}

@Composable fun DashboardScreen() {
    Text("📊 Dashboard Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun TripsScreen() {
    Text("🛣 Trips Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun TrucksScreen() {
    Text("🚛 Trucks Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun DriversScreen() {
    Text("👷 Drivers Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable fun ExpensesScreen() {
    Text("💰 Expenses Screen", style = MaterialTheme.typography.headlineMedium)
}
