package com.example.smartretailph.ui.reports

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.*

enum class ReportPeriod {
    TODAY, WEEK, MONTH, YEAR
}

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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Summary", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total revenue: \$${"%.2f".format(state.totalRevenue)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Total orders: ${state.totalOrders}", style = MaterialTheme.typography.bodySmall)
                    Text("Total products: ${state.totalProducts}", style = MaterialTheme.typography.bodySmall)
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
                        Text("⚠️ Stock Replenishment Needed", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        state.lowStockItems.forEach { (name, qty) ->
                            Text("$name: $qty units remaining", style = MaterialTheme.typography.bodySmall)
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
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
            items(state.salesByDay.entries.sortedByDescending { it.key }.take(14).toList()) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(entry.key, style = MaterialTheme.typography.bodyMedium)
                        Text("\$${"%.2f".format(entry.value)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Top products
        if (state.topProducts.isNotEmpty()) {
            item {
                Text("Top Products", style = MaterialTheme.typography.titleMedium)
            }
            items(state.topProducts.take(5)) { (name, qty) ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(name, style = MaterialTheme.typography.bodyMedium)
                        Text("Qty: $qty", style = MaterialTheme.typography.bodySmall)
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
                    Text("\$${"%.2f".format(state.forecastNextDay)}", style = MaterialTheme.typography.bodyLarge)
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                                .background(androidx.compose.ui.graphics.Color.Blue)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Sales")
                    }

                    Spacer(Modifier.width(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                                .background(androidx.compose.ui.graphics.Color.Green)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Orders")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LineGraph(last7)
        }
    }
}

@Composable
private fun LineGraph(data: List<Map.Entry<String, Double>>) {

    val maxValue = data.maxOf { it.value }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {

        val width = size.width
        val height = size.height

        val stepX = width / (data.size - 1)

        var previousX = 0f
        var previousY = 0f

        data.forEachIndexed { index, entry ->

            val x = index * stepX
            val y = height - (entry.value / maxValue * height).toFloat()

            drawCircle(
                color = androidx.compose.ui.graphics.Color.Blue,
                radius = 8f,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )

            if (index > 0) {
                drawLine(
                    color = androidx.compose.ui.graphics.Color.Blue,
                    start = androidx.compose.ui.geometry.Offset(previousX, previousY),
                    end = androidx.compose.ui.geometry.Offset(x, y),
                    strokeWidth = 4f
                )
            }

            previousX = x
            previousY = y
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

                        val percent = (entry.value / total) * 100

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
    colors: List<androidx.compose.ui.graphics.Color>
) {

    val total = categoryData.values.sum()

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
    ) {

        var startAngle = -90f

        categoryData.entries.forEachIndexed { index, entry ->

            val sweep = (entry.value / total * 360).toFloat()

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )

            startAngle += sweep
        }
    }
}

