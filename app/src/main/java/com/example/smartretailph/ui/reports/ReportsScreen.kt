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
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    reportsViewModel: ReportsViewModel = viewModel()
) {
    val state by reportsViewModel.state.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Reports & Analytics", style = MaterialTheme.typography.titleLarge)
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

        // Sales chart (last 14 days)
        if (state.salesByDay.isNotEmpty()) {
            item {
                Text("Sales Chart (Last 14 Days)", style = MaterialTheme.typography.titleMedium)
            }
            item {
                SimpleSalesChart(state.salesByDay)
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
private fun SimpleSalesChart(salesByDay: Map<String, Double>) {
    val last14 = salesByDay.entries.sortedBy { it.key }.takeLast(14)
    if (last14.isEmpty()) return

    val maxValue = last14.maxOf { it.value }
    val chartHeight = 200.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = androidx.compose.ui.Alignment.Bottom
        ) {
            last14.forEach { entry ->
                val barHeight = if (maxValue > 0) (entry.value.toDouble() / maxValue.toDouble() * chartHeight.value).dp else 0.dp
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(barHeight)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        entry.key.takeLast(5),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Max: $${"%,.0f".format(maxValue)}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(androidx.compose.ui.Alignment.End)
        )
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

