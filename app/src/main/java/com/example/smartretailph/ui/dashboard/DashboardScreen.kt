package com.example.smartretailph.ui.dashboard

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartretailph.viewmodel.DashboardViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.times

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = viewModel(),
    onNavigateToInventory: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCart: () -> Unit = {}
) {
    val state by dashboardViewModel.state.collectAsState()

    var showReportsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val quickActions = listOf(
        QuickAction(
            "Add Product",
            Icons.Default.AddShoppingCart,
            listOf(Color(0xFF2563EB), Color(0xFF3B82F6)),
            onNavigateToInventory
        ),
        QuickAction(
            "New Sale",
            Icons.Default.Inventory2,
            listOf(Color(0xFF9333EA), Color(0xFFC084FC)),
            onNavigateToInventory
        ),
        QuickAction(
            "Stock Alert",
            Icons.Default.Warning,
            listOf(Color(0xFF16A34A), Color(0xFF4ADE80)),
            onNavigateToInventory
        ),
        QuickAction(
            "View Reports",
            Icons.Default.SsidChart,
            listOf(Color(0xFFF97316), Color(0xFFFB923C)),
            { showReportsSheet = true }
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF2563EB),
                                Color(0xFF1D4ED8)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {

                Column {

                    Text(
                        text = "Welcome back, User! 👋",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Here's your inventory overview for today",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OverviewCard(
                        title = "Total Items",
                        value = state.totalProducts.toString(),
                        icon = Icons.Default.Inventory2,
                        background = Color(0xFFDCEAFE),
                        modifier = Modifier.weight(1f)
                    )

                    OverviewCard(
                        title = "Total Value",
                        value = "₱${"%.2f".format(state.totalRevenue)}",
                        icon = Icons.Default.SsidChart,
                        background = Color(0xFFD1FAE5),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OverviewCard(
                        title = "Low Stock",
                        value = state.lowStock.toString(),
                        icon = Icons.Default.Warning,
                        background = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    )

                    OverviewCard(
                        title = "Orders",
                        value = state.todaysOrdersCount.toString(),
                        icon = Icons.Default.ReceiptLong,
                        background = Color(0xFFEDE9FE),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(((quickActions.size + 1) / 2) * 120.dp)
            ) {

                items(quickActions) { action ->

                    GradientActionCard(
                        title = action.title,
                        icon = action.icon,
                        colors = action.colors,
                        onClick = action.action
                    )
                }
            }
        }

        // Recent Activity
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View All",
                    color = Color(0xFF2563EB)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            ActivityCard("Product Added", "Coca-Cola 500ml", "2h ago")
        }

        item {
            ActivityCard("Stock Updated", "Whole Milk 1L", "4h ago")
        }

        item {
            ActivityCard("Low Stock Alert", "White Bread", "6h ago")
        }
    }

    if (showReportsSheet) {

        ModalBottomSheet(
            onDismissRequest = { showReportsSheet = false },
            sheetState = sheetState
        ) {

            ReportsBottomSheet(
                onGenerateReports = {
                    showReportsSheet = false
                    onNavigateToReports()
                }
            )

        }
    }
}

@Composable
fun OverviewCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color.White.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActivityCard(
    title: String,
    subtitle: String,
    time: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun GradientActionCard(
    title: String,
    icon: ImageVector,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = onClick
    ) {

        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReportsBottomSheet(
    onGenerateReports: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Reports & Analytics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Date Range",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Last 7 Days",
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            OverviewCard(
                title = "Revenue",
                value = "₱24.5K",
                icon = Icons.Default.SsidChart,
                background = Color(0xFFD1FAE5),
                modifier = Modifier.weight(1f)
            )

            OverviewCard(
                title = "Sales",
                value = "892",
                icon = Icons.Default.ReceiptLong,
                background = Color(0xFFDCEAFE),
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = onGenerateReports,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {

            Icon(
                Icons.Default.Report,
                contentDescription = null
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text("Generate Report")
        }
    }
}
