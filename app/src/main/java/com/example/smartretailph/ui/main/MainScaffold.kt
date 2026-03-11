package com.example.smartretailph.ui.main

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.smartretailph.ui.dashboard.DashboardScreen
import com.example.smartretailph.ui.inventory.InventoryScreen
import com.example.smartretailph.ui.navigation.MainRoutes
import com.example.smartretailph.ui.notifications.NotificationsBottomSheet
import com.example.smartretailph.ui.orders.OrdersScreen
import com.example.smartretailph.ui.reports.ReportsScreen
import kotlinx.coroutines.launch
import com.example.smartretailph.ui.inventory.InventoryManagementScreen

enum class MainTab(
    val route: String,
    val title: String
) {
    DASHBOARD(MainRoutes.DASHBOARD, "Home"),
    INVENTORY(MainRoutes.INVENTORY, "Inventory"),
    ORDERS(MainRoutes.ORDERS, "Orders"),
    REPORTS(MainRoutes.REPORTS, "Reports")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onLogout: () -> Unit
) {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val context = LocalContext.current
    val versionName = remember {
        context.packageManager
            .getPackageInfo(context.packageName, 0).versionName
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MainRoutes.DASHBOARD

    val currentTopBarTitle = when (currentRoute) {
        MainRoutes.DASHBOARD -> "Dashboard"
        MainRoutes.INVENTORY -> "Inventory"
        MainRoutes.ORDERS -> "Orders"
        MainRoutes.REPORTS -> "Reports"
        else -> "SmartRetailPH"
    }

    val mainTabs = listOf(
        MainTab.DASHBOARD,
        MainTab.INVENTORY,
        MainTab.ORDERS,
        MainTab.REPORTS
    )

    var showNotifications by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                // HEADER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(20.dp)
                ) {

                    Text(
                        text = "Juan Dela Cruz",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        text = "Store Manager",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text(
                    text = "Main Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                // ACCOUNT SECTION
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile Settings") },
                    selected = false,
                    onClick = {}
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Notifications") },
                    selected = false,
                    onClick = {}
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                    label = { Text("Preferences") },
                    selected = false,
                    onClick = {}
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // APP SECTION
                Text(
                    text = "App",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
                    label = { Text("Inventory Management") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(MainRoutes.INVENTORY_MANAGEMENT)
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Security, contentDescription = null) },
                    label = { Text("Privacy & Security") },
                    selected = false,
                    onClick = {}
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Help, contentDescription = null) },
                    label = { Text("Help & Support") },
                    selected = false,
                    onClick = {}
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("About (v$versionName)") },
                    selected = false,
                    onClick = {}
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    label = { Text("Log Out") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {

        Scaffold(

            topBar = {

                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 6.dp,   // stronger but still soft shadow
                    color = Color.White
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)   // increased height
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // MENU
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(26.dp) // bigger icon
                            )
                        }

                        // TITLE
                        Text(
                            text = currentTopBarTitle,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF111827)
                        )

                        // SEARCH
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // NOTIFICATIONS
                        Box {

                            IconButton(onClick = { showNotifications = true }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color(0xFF111827),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                            )
                        }
                    }
                }
            },

            bottomBar = {

                NavigationBar(
                    containerColor = Color(0xFFF5F5F5),
                    tonalElevation = 8.dp
                ) {

                    mainTabs.forEach { tab ->

                        val selected = currentRoute == tab.route

                        val icon = when (tab) {
                            MainTab.DASHBOARD -> Icons.Default.Home
                            MainTab.INVENTORY -> Icons.Default.Inventory2
                            MainTab.ORDERS -> Icons.Default.ShoppingCart
                            MainTab.REPORTS -> Icons.Default.BarChart
                        }

                        NavigationBarItem(
                            selected = selected,

                            onClick = {

                                navController.navigate(tab.route) {

                                    launchSingleTop = true

                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }

                                    restoreState = true
                                }
                            },

                            icon = {

                                Icon(
                                    icon,
                                    contentDescription = tab.title,
                                    tint = if (selected)
                                        Color(0xFF2563EB)  // Blue active
                                    else
                                        Color(0xFF9CA3AF)  // Gray inactive
                                )
                            },

                            label = {

                                Text(
                                    tab.title,
                                    color = if (selected)
                                        Color(0xFF2563EB)
                                    else
                                        Color(0xFF9CA3AF)
                                )
                            },

                            alwaysShowLabel = true,

                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }

        ) { innerPadding ->

            Box(modifier = Modifier.padding(innerPadding)) {

                NavHost(
                    navController = navController,
                    startDestination = MainRoutes.DASHBOARD
                ) {

                    composable(MainRoutes.DASHBOARD) {

                        DashboardScreen(
                            onNavigateToInventory = {
                                navController.navigate(MainRoutes.INVENTORY) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToOrders = {
                                navController.navigate(MainRoutes.ORDERS) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToReports = {
                                navController.navigate(MainRoutes.REPORTS) {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToCart = {
                                navController.navigate(MainRoutes.INVENTORY) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(MainRoutes.INVENTORY) { InventoryScreen() }

                    composable(MainRoutes.ORDERS) { OrdersScreen() }

                    composable(MainRoutes.REPORTS) { ReportsScreen() }

                    composable(MainRoutes.INVENTORY_MANAGEMENT) {
                        InventoryManagementScreen()
                    }
                }

                if (showNotifications) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                            .clickable { showNotifications = false }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        NotificationsBottomSheet(
                            onDismiss = { showNotifications = false }
                        )
                    }
                }
            }
        }
    }
}