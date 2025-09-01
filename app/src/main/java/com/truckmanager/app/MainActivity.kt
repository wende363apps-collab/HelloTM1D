package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DashboardScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    // Track which menu item is selected
    var selectedMenu by remember { mutableStateOf("Revenue") }

    val revenue = 120000.0
    val expenses = 45000.0
    val netIncome = revenue - expenses

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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatCard(
                title = "Revenue",
                value = "$revenue Birr",
                isSelected = selectedMenu == "Revenue",
                onClick = { selectedMenu = "Revenue" }
            )
            StatCard(
                title = "Expenses",
                value = "$expenses Birr",
                isSelected = selectedMenu == "Expenses",
                onClick = { selectedMenu = "Expenses" }
            )
            StatCard(
                title = "Net Income",
                value = "$netIncome Birr",
                isSelected = selectedMenu == "Net Income",
                onClick = { selectedMenu = "Net Income" }
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFFD0E8FF) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        onClick = { onClick() }
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
