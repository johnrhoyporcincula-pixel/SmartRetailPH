package com.example.smartretailph.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartretailph.ui.dashboard.DashboardScreen
import com.example.smartretailph.ui.inventory.InventoryScreen
import com.example.smartretailph.ui.navigation.MainRoutes
import com.example.smartretailph.ui.notifications.NotificationsBottomSheet
import com.example.smartretailph.ui.orders.OrdersScreen
import com.example.smartretailph.ui.reports.ReportsScreen
import kotlinx.coroutines.launch

enum class MainTab(
    val route: String,
    val title: String
) {
    DASHBOARD(MainRoutes.DASHBOARD, "Dashboard"),
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)

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
                Text(
                    text = "Main Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { /* TODO: Profile */ }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* TODO: Settings */ }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
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
                CenterAlignedTopAppBar(
                    title = { Text(currentTopBarTitle, color = MaterialTheme.colorScheme.onPrimary) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showNotifications = true }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    mainTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
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
                                val icon = when (tab) {
                                    MainTab.DASHBOARD -> Icons.Default.Dashboard
                                    MainTab.INVENTORY -> Icons.Default.Inventory2
                                    MainTab.ORDERS -> Icons.Default.ReceiptLong
                                    MainTab.REPORTS -> Icons.Default.SsidChart
                                }
                                Icon(icon, contentDescription = tab.title)
                            },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = MainRoutes.DASHBOARD,
                    modifier = Modifier
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
                }

                if (showNotifications) {
                    // Dimmed backdrop that dismisses when tapped
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                            .clickable { showNotifications = false }
                    )

                    // Bottom sheet content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        NotificationsBottomSheet(onDismiss = { showNotifications = false })
                    }
                }
            }
        }
    }
}

