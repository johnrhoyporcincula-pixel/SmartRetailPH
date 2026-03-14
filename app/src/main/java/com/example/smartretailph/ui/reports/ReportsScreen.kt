package com.example.smartretailph.ui.reports

import androidx.compose.foundation.layout.size
import com.example.smartretailph.ui.dashboard.OverviewCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartretailph.viewmodel.ReportsViewModel
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class ReportPeriod {
    TODAY, WEEK, MONTH, YEAR
}

data class ReportsMetric(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val background: Color
)

@Composable
fun ReportsScreen(
    reportsViewModel: ReportsViewModel = viewModel()
) {
    val state by reportsViewModel.state.collectAsState()
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.TODAY) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(onClick = { selectedPeriod = ReportPeriod.TODAY }) {
                        Text("Today")
                    }

                    Button(onClick = { selectedPeriod = ReportPeriod.WEEK }) {
                        Text("This Week")
                    }

                    Button(onClick = { selectedPeriod = ReportPeriod.MONTH }) {
                        Text("This Month")
                    }

                    Button(onClick = { selectedPeriod = ReportPeriod.YEAR }) {
                        Text("This Year")
                    }
                }
            }
        }

        // Summary metrics
        item {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Reports & Analytics",
                    style = MaterialTheme.typography.titleMedium
                )

                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OverviewCard(
                        title = "Total Revenue",
                        value = "₱${"%.2f".format(state.totalRevenue)}",
                        icon = Icons.Default.SsidChart,
                        background = Color(0xFFD1FAE5),
                        modifier = Modifier.weight(1f)
                    )

                    OverviewCard(
                        title = "Orders",
                        value = state.totalOrders.toString(),
                        icon = Icons.Default.ReceiptLong,
                        background = Color(0xFFDCEAFE),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OverviewCard(
                        title = "Items Sold",
                        value = state.totalProducts.toString(),
                        icon = Icons.Default.Inventory2,
                        background = Color(0xFFEDE9FE),
                        modifier = Modifier.weight(1f)
                    )

                    OverviewCard(
                        title = "Avg Order",
                        value = if (state.totalOrders > 0)
                            "₱${"%.2f".format(state.totalRevenue / state.totalOrders)}"
                        else "₱0.00",
                        icon = Icons.Default.AttachMoney,
                        background = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Export button
        item {
            Button(
                onClick = { exportSalesCSV(context, state.salesByDay) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = "Export")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Sales CSV")
            }
        }

        // Low stock alerts
        if (state.lowStockItems.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // Use a red-tinted container for alerts
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "⚠️ Stock Replenishment Needed",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        state.lowStockItems.forEach { (name, qty) ->
                            Text(
                                "$name: $qty units remaining",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Sales overview graph
        if (state.salesByDay.isNotEmpty()) {
            item {
                SalesOverviewGraph(state.salesByDay)
            }
        }

        // Sales by category
        if (state.salesByCategory.isNotEmpty()) {
            item {
                SalesByCategoryChart(state.salesByCategory)
            }
        } else {
            item {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        "No category sales data available",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Sales by day list
        if (state.salesByDay.isNotEmpty()) {
            item {
                Text("Sales by Day", style = MaterialTheme.typography.titleMedium)
            }
            items(
                state.salesByDay.entries.sortedByDescending { it.key }.take(14).toList()
            ) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(entry.key, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "\$${"%.2f".format(entry.value)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Top selling products (Figma style)
        if (state.topProducts.isNotEmpty()) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Top Selling Products",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        state.topProducts.take(5).forEachIndexed { index, product ->

                            val name = product.first
                            val qty = product.second

                            val revenue = qty * 12 // simple demo calculation

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF1F5F9)
                                ),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Rank number square
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                color = Color(0xFF2563EB),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = "$qty units sold",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF6B7280)
                                        )
                                    }

                                    Text(
                                        text = "₱${"%.2f".format(revenue)}",
                                        color = Color(0xFF059669),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Forecast
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Forecast (Next Day)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "\$${"%.2f".format(state.forecastNextDay)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("Based on 7-day average", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SalesOverviewGraph(
    salesByDay: Map<String, Double>
) {

    val last7 = salesByDay.entries.sortedBy { it.key }.takeLast(7)

    if (last7.isEmpty()) return

    val labels = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    "Sales Overview",
                    style = MaterialTheme.typography.titleMedium
                )

                Row {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .width(10.dp)
                                .height(10.dp)
                                .background(Color(0xFF3B82F6))
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Sales")
                    }

                    Spacer(Modifier.width(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .width(10.dp)
                                .height(10.dp)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Orders")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SalesLineChart(last7)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun SalesLineChart(data: List<Map.Entry<String, Double>>) {

    val maxValue = data.maxOf { it.value }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {

        val width = size.width
        val height = size.height

        val stepX = width / (data.size - 1)

        // grid lines
        val gridLines = 4

        repeat(gridLines) { i ->

            val y = height / gridLines * i

            drawLine(
                color = Color(0xFFE5E7EB),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 2f
            )
        }

        var previous = Offset.Zero

        data.forEachIndexed { index, entry ->

            val x = stepX * index
            val y = height - (entry.value / maxValue * height).toFloat()

            val current = Offset(x, y)

            if (index > 0) {
                drawLine(
                    color = Color(0xFF3B82F6),
                    start = previous,
                    end = current,
                    strokeWidth = 4f
                )
            }

            drawCircle(
                color = Color(0xFF3B82F6),
                radius = 6f,
                center = current
            )

            previous = current
        }
    }
}

private fun exportSalesCSV(context: Context, salesByDay: Map<String, Double>) {
    val csv = StringBuilder()
    csv.append("Date,Amount\n")
    salesByDay.entries.sortedBy { it.key }.forEach { (date, amount) ->
        csv.append("$date,%.2f\n".format(amount))
    }

    val fname = "sales_${System.currentTimeMillis()}.csv"
    runCatching {
        context.openFileOutput(fname, Context.MODE_PRIVATE).use { fos ->
            fos.write(csv.toString().toByteArray())
        }
        Toast.makeText(context, "Exported to $fname", Toast.LENGTH_SHORT).show()
    }.onFailure {
        Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun SalesByCategoryChart(
    categoryData: Map<String, Double>
) {

    val total = categoryData.values.sum()

    val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFF4285F4),
        androidx.compose.ui.graphics.Color(0xFF34A853),
        androidx.compose.ui.graphics.Color(0xFFFBBC05),
        androidx.compose.ui.graphics.Color(0xFFEA4335),
        androidx.compose.ui.graphics.Color(0xFF9C27B0),
        androidx.compose.ui.graphics.Color(0xFF00ACC1)
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                "Sales by Category",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                CategoryPieChart(categoryData, colors)

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    categoryData.entries.forEachIndexed { index, entry ->

                        val percent = if (total > 0) (entry.value / total) * 100 else 0.0

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(12.dp)
                                    .background(colors[index % colors.size])
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "${entry.key} - ${"%.1f".format(percent)}%"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPieChart(
    categoryData: Map<String, Double>,
    colors: List<Color>
) {

    val total = categoryData.values.sum()

    Canvas(
        modifier = Modifier
            .size(150.dp)
    ) {

        var startAngle = -90f

        categoryData.entries.forEachIndexed { index, entry ->

            val sweep = (entry.value / total * 360).toFloat()

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 40f
                )
            )

            startAngle += sweep
        }
    }
}

