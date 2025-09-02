package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") { DashboardScreen(navController) }
        composable("trips") { TripListScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("TM1D Dashboard 🚛") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = { navController.navigate("trips") }) {
                Text("Go to Trips")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen() {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Trips") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Here we will show trip data from Room DB")
        }
    }
}
