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
                    ACCOUNT SECTION
                    -------------------------
                    */

                    Text(
                        "ACCOUNT",
                        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
                        color = Color.Gray
                    )

                    DrawerItem(Icons.Default.Person, "Profile Settings")

                    DrawerItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        badge = "3"
                    )

                    DrawerItem(Icons.Default.Settings, "Preferences")

                    Spacer(modifier = Modifier.height(12.dp))

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

                    DrawerItem(Icons.Default.Security, "Privacy & Security")
                    DrawerItem(Icons.Default.Help, "Help & Support")
                    DrawerItem(Icons.Default.Info, "About (v$versionName)")

                    Spacer(modifier = Modifier.weight(1f))

                    /*
                    -------------------------
                    LOGOUT BUTTON
                    -------------------------
                    */

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    onLogout()
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.Logout,
                                contentDescription = null,
                                tint = Color.Red
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                "Log Out",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
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
                    NotificationsOverlay(
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
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F4F6)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(icon, contentDescription = null)

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )

            if (badge != null) {

                Box(
                    modifier = Modifier
                        .background(Color.Red, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
