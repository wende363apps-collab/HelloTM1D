package com.truckmanager.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: TripViewModel = viewModel()
            val dark by vm.isDarkTheme.observeAsState(false)

            MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
                AppScaffold(vm = vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(vm: TripViewModel) {
    val nav = rememberNavController()
    val items = listOf(
        BottomItem("dashboard", "Dashboard", Icons.Filled.Home),
        BottomItem("trips", "Trips", Icons.Filled.List),
        BottomItem("settings", "Settings", Icons.Filled.Settings),
    )
    var current by remember { mutableStateOf("dashboard") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TM1D") },
                actions = {
                    if (current == "trips") {
                        IconButton(onClick = { shareCsv(vm) }) { Icon(Icons.Filled.Share, null) }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val route = current
                items.forEach { bi ->
                    NavigationBarItem(
                        selected = route == bi.route,
                        onClick = {
                            current = bi.route
                            nav.navigate(bi.route) { launchSingleTop = true; popUpTo(nav.graph.startDestinationId) { inclusive = false } }
                        },
                        icon = { Icon(bi.icon, contentDescription = bi.label) },
                        label = { Text(bi.label) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (current == "trips") {
                FloatingActionButton(onClick = { nav.navigate("tripForm") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") { DashboardScreen(vm) }
            composable("trips") { TripsScreen(vm, onOpen = { id -> nav.navigate("detail/$id") }) }
            composable("tripForm") { TripFormScreen(vm, onDone = { nav.popBackStack() }) }
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { back ->
                TripDetailScreen(
                    vm = vm,
                    id = back.arguments?.getInt("id") ?: 0,
                    onBack = { nav.popBackStack() }
                )
            }
            composable("settings") { SettingsScreen(vm) }
        }
    }
}

data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun DashboardScreen(vm: TripViewModel) {
    val inc by vm.totalIncome.observeAsState(0.0)
    val cost by vm.totalCost.observeAsState(0.0)
    val net = (inc ?: 0.0) - (cost ?: 0.0)

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        StatsCard("Revenue", "%.2f Birr".format(inc ?: 0.0))
        StatsCard("Expenses", "%.2f Birr".format(cost ?: 0.0))
        StatsCard("Net Income", "%.2f Birr".format(net))
    }
}

@Composable
fun StatsCard(title: String, value: String) {
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun TripsScreen(vm: TripViewModel, onOpen: (Int) -> Unit) {
    val query by vm.query.observeAsState("")
    val trips by vm.tripsPaged.observeAsState(emptyList())
    val sort by vm.sort.observeAsState("date")
    val snackbarHost = remember { SnackbarHostState() }
    var lastDeleted by remember { mutableStateOf<Trip?>(null) }

    LaunchedEffect(Unit) { vm.loadFirstPage() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Search + sort row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { vm.query.value = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Search name/destination") }
                )
                SortMenu(current = sort, onChange = { vm.sort.value = it; vm.loadFirstPage() })
                IconButton(onClick = { shareCsv(vm) }) { Icon(Icons.Filled.Download, contentDescription = "Export CSV") }
            }

            if (trips.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No trips yet. Tap + to add.")
                }
            } else {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trips, key = { it.id }) { trip ->
                        Card(
                            Modifier.fillMaxWidth().clickable { onOpen(trip.id) },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(trip.name, style = MaterialTheme.typography.titleMedium)
                                    Text("${trip.destination} • ${trip.distance} km")
                                    Text("Income ${trip.income} • Cost ${trip.cost}")
                                }
                                TextButton(onClick = {
                                    lastDeleted = trip
                                    vm.deleteTrip(trip)
                                    // Snackbar with undo
                                    LaunchedEffect(trip.id) {
                                        val res = snackbarHost.showSnackbar(
                                            message = "Trip deleted",
                                            actionLabel = "UNDO",
                                            withDismissAction = true
                                        )
                                        if (res == SnackbarResult.ActionPerformed && lastDeleted != null) {
                                            vm.addTrip(
                                                name = lastDeleted!!.name,
                                                destination = lastDeleted!!.destination,
                                                distance = lastDeleted!!.distance,
                                                income = lastDeleted!!.income,
                                                cost = lastDeleted!!.cost,
                                                date = lastDeleted!!.date
                                            )
                                            lastDeleted = null
                                        }
                                    }
                                }) { Text("Delete") }
                            }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            OutlinedButton(onClick = { vm.loadNextPage() }) { Text("Load more") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortMenu(current: String, onChange: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { open = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Sort: $current")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            listOf("date", "name", "destination", "distance").forEach { key ->
                DropdownMenuItem(
                    text = { Text(key) },
                    onClick = { onChange(key); open = false }
                )
            }
        }
    }
}

@Composable
fun TripFormScreen(vm: TripViewModel, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val num = KeyboardOptions(keyboardType = KeyboardType.Number)

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Add Trip", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(name, { name = it }, label = { Text("Truck/Driver") })
        OutlinedTextField(destination, { destination = it }, label = { Text("Destination") })
        OutlinedTextField(distance, { distance = it }, label = { Text("Distance (km)") }, keyboardOptions = num)
        OutlinedTextField(income, { income = it }, label = { Text("Income (Birr)") }, keyboardOptions = num)
        OutlinedTextField(cost, { cost = it }, label = { Text("Cost (Birr)") }, keyboardOptions = num)
        OutlinedTextField(date, { date = it }, label = { Text("Date (e.g. 2025-09-22)") })

        val valid = name.isNotBlank() && destination.isNotBlank()
        Button(
            onClick = {
                vm.addTrip(
                    name = name.trim(),
                    destination = destination.trim(),
                    distance = distance.toDoubleOrNull() ?: 0.0,
                    income = income.toDoubleOrNull() ?: 0.0,
                    cost = cost.toDoubleOrNull() ?: 0.0,
                    date = date.ifBlank { "N/A" }
                )
                onDone()
            },
            enabled = valid,
            modifier = Modifier.align(Alignment.End)
        ) { Text("Save") }
    }
}

@Composable
fun TripDetailScreen(vm: TripViewModel, id: Int, onBack: () -> Unit) {
    val pageTrips by vm.tripsPaged.observeAsState(emptyList())
    // Ensure the page containing this id is loaded:
    LaunchedEffect(id) { vm.loadFirstPage() }

    val t = pageTrips.find { it.id == id }
    if (t == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Loading...") }
        return
    }

    var name by remember { mutableStateOf(t.name) }
    var destination by remember { mutableStateOf(t.destination) }
    var distance by remember { mutableStateOf(t.distance.toString()) }
    var income by remember { mutableStateOf(t.income.toString()) }
    var cost by remember { mutableStateOf(t.cost.toString()) }
    var date by remember { mutableStateOf(t.date) }
    val num = KeyboardOptions(keyboardType = KeyboardType.Number)

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Trip Details", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(name, { name = it }, label = { Text("Truck/Driver") })
        OutlinedTextField(destination, { destination = it }, label = { Text("Destination") })
        OutlinedTextField(distance, { distance = it }, label = { Text("Distance (km)") }, keyboardOptions = num)
        OutlinedTextField(income, { income = it }, label = { Text("Income (Birr)") }, keyboardOptions = num)
        OutlinedTextField(cost, { cost = it }, label = { Text("Cost (Birr)") }, keyboardOptions = num)
        OutlinedTextField(date, { date = it }, label = { Text("Date") })

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                vm.updateTrip(
                    t.copy(
                        name = name.trim(),
                        destination = destination.trim(),
                        distance = distance.toDoubleOrNull() ?: 0.0,
                        income = income.toDoubleOrNull() ?: 0.0,
                        cost = cost.toDoubleOrNull() ?: 0.0,
                        date = date
                    )
                )
                onBack()
            }) { Text("Save") }
            OutlinedButton(onClick = onBack) { Text("Cancel") }
        }
    }
}

@Composable
fun SettingsScreen(vm: TripViewModel) {
    val dark by vm.isDarkTheme.observeAsState(false)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Theme", modifier = Modifier.weight(1f))
            Switch(checked = dark, onCheckedChange = { vm.setDarkTheme(it) })
        }
    }
}

// --- CSV Export / Import (Export: share as plain text CSV)

@Composable
private fun shareCsv(vm: TripViewModel) {
    val ctx = LocalContext.current
    val data by vm.tripsPaged.observeAsState(emptyList())

    LaunchedEffect(data) {
        val header = "id,name,destination,distance,income,cost,date"
        val rows = data.joinToString("\n") { t ->
            listOf(t.id, t.name, t.destination, t.distance, t.income, t.cost, t.date)
                .joinToString(",")
        }
        val csv = "$header\n$rows"
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "TM1D Trips Export")
            putExtra(Intent.EXTRA_TEXT, csv)
        }
        ctx.startActivity(Intent.createChooser(send, "Share Trips CSV"))
    }
}
