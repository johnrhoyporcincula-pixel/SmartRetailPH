package com.example.smartretailph.ui.main

import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.smartretailph.ui.dashboard.DashboardScreen
import com.example.smartretailph.ui.inventory.InventoryScreen
import com.example.smartretailph.ui.navigation.MainRoutes
import com.example.smartretailph.ui.notifications.NotificationsOverlay
import com.example.smartretailph.ui.orders.OrdersScreen
import com.example.smartretailph.ui.reports.ReportsScreen
import kotlinx.coroutines.launch
import com.example.smartretailph.ui.inventory.InventoryManagementScreen
import android.widget.Toast
import com.example.smartretailph.viewmodel.NotificationsViewModel

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
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val context = LocalContext.current
    val versionName = remember {
        context.packageManager
            .getPackageInfo(context.packageName, 0).versionName ?: "1.0"
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MainRoutes.DASHBOARD

    var showNotifications by remember { mutableStateOf(false) }

    // 🔥 FIX: auto-close when navigating
    LaunchedEffect(currentRoute) {
        showNotifications = false
    }

    val currentTopBarTitle = when (currentRoute) {
        MainRoutes.DASHBOARD -> "Dashboard"
        MainRoutes.INVENTORY -> "Inventory"
        MainRoutes.ORDERS -> "Orders"
        MainRoutes.REPORTS -> "Reports"

        MainRoutes.PROFILE -> "Profile"
        MainRoutes.PREFERENCES -> "Preferences"
        MainRoutes.SECURITY -> "Security"
        MainRoutes.HELP -> "Help & Support"
        MainRoutes.ABOUT -> "About"

        else -> "SmartRetailPH"
    }

    val mainTabs = listOf(
        MainTab.DASHBOARD,
        MainTab.INVENTORY,
        MainTab.ORDERS,
        MainTab.REPORTS
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 16.dp)
                ) {

                    /*
                    -------------------------
                    HEADER (BLUE PROFILE)
                    -------------------------
                    */

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2563EB),
                                        Color(0xFF1D4ED8)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {

                                Text(
                                    text = "Alex Thompson",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "alex@retailbase.com",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        "Store Manager",
                                        color = Color.White,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 4.dp
                                        ),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    /*
                    -------------------------
                    APP SECTION
                    -------------------------
                    */

                    Text(
                        "APP",
                        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
                        color = Color.Gray
                    )

                    DrawerItem(Icons.Default.Inventory2, "Inventory Management") {
                        scope.launch { drawerState.close() }
                        navController.navigate(MainRoutes.INVENTORY_MANAGEMENT)
                    }

                    DrawerItem(Icons.Default.Security, "Privacy & Security") {
                        scope.launch { drawerState.close() }
                        navController.navigate(MainRoutes.SECURITY)
                    }
                    DrawerItem(Icons.Default.Help, "Help & Support") {
                        scope.launch { drawerState.close() }
                        navController.navigate(MainRoutes.HELP)
                    }
                    DrawerItem(Icons.Default.Info, "About (v$versionName)") {
                        scope.launch { drawerState.close() }
                        navController.navigate(MainRoutes.ABOUT)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(14.dp),
                        onClick = {
                            scope.launch { drawerState.close() }
                            onLogout()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                "Exit SmartRetailPH",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    ) {

        Scaffold(
            modifier = modifier,
            topBar = {

                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

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
                                contentDescription = "Menu"
                            )
                        }

                        Text(
                            text = currentTopBarTitle,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            style = MaterialTheme.typography.titleLarge
                        )

                        IconButton(onClick = {
                            Toast.makeText(
                                context,
                                "Search coming soon",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }

                        // 🔥 Notification with badge logic
                        BadgedBox(
                            badge = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            CircleShape
                                        )
                                )
                            }
                        ) {
                            IconButton(onClick = { showNotifications = true }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
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
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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

                    // NEW DRAWER SCREENS
                    composable(MainRoutes.PROFILE) {
                        ProfileScreen()
                    }

                    composable(MainRoutes.PREFERENCES) {
                        PreferencesScreen()
                    }

                    composable(MainRoutes.HELP) {
                        HelpScreen()
                    }

                    composable(MainRoutes.ABOUT) {
                        AboutScreen(versionName)
                    }
                }

                if (showNotifications) {
                    val notificationsViewModel: NotificationsViewModel = remember {
                        NotificationsViewModel()
                    }

                    NotificationsOverlay(
                        viewModel = notificationsViewModel,
                        onDismiss = { showNotifications = false }
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            badge?.let {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        it,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
