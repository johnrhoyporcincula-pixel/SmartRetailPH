package com.example.smartretailph.ui.reports

import androidx.compose.foundation.layout.size
import com.example.smartretailph.ui.dashboard.OverviewCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.remember

val ChartColors = listOf(
    Color(0xFF4285F4),
    Color(0xFF34A853),
    Color(0xFFFBBC05),
    Color(0xFFEA4335),
    Color(0xFF9C27B0),
    Color(0xFF00ACC1)
)

enum class ReportPeriod {
    TODAY, WEEK, MONTH, YEAR
}

@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier,
    reportsViewModel: ReportsViewModel = viewModel()
) {
    val state by reportsViewModel.state.collectAsState()
    val selectedPeriod by reportsViewModel.selectedPeriod.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 🔹 PERIOD BUTTONS
        item {

            val periods = listOf(
                "Today" to ReportPeriod.TODAY,
                "This Week" to ReportPeriod.WEEK,
                "This Month" to ReportPeriod.MONTH,
                "This Year" to ReportPeriod.YEAR
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { (label, period) ->
                    ReportPeriodButton(
                        text = label,
                        period = period,
                        selectedPeriod = selectedPeriod,
                        onClick = { reportsViewModel.setPeriod(period) }
                    )
                }
            }
        }

        // 🔹 OVERVIEW CARDS
        item {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Reports & Analytics",
                    style = MaterialTheme.typography.titleMedium
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 2
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

        // 🔹 SALES BY CATEGORY (ONLY GRAPH LEFT)
        item {
            CategoryInsightsSection(
                top = state.topCategory,
                worst = state.worstCategory
            )
        }

        item {
            SalesByCategoryChart(state.salesByCategory)
        }

        item {
            ProductInsightsSection(
                best = state.bestProduct,
                worst = state.worstProduct
            )
        }

        item {
            PredictiveSection(
                forecast = state.forecastNextDay,
                trend = state.salesTrendPercent,
                restock = state.restockPredictions
            )
        }
    }
}

@Composable
private fun SalesByCategoryChart(
    categoryData: Map<String, Double>,
    modifier: Modifier = Modifier
) {

    val chartData = remember(categoryData) {
        val total = categoryData.values.sum()

        categoryData.entries.mapIndexed { index, entry ->
            Triple(
                entry,
                if (total > 0) (entry.value / total) * 100 else 0.0,
                index
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Sales by Category",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                CategoryPieChart(
                    categoryData = categoryData,
                    colors = ChartColors
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    chartData.forEach { triple ->

                        val entry = triple.first
                        val percent = triple.second
                        val index = triple.third

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(ChartColors[index % ChartColors.size])
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${entry.key} - ${"%.1f".format(percent)}%"
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
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = categoryData.values.sum()

    Canvas(
        modifier = modifier.size(150.dp)
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

@Composable
private fun ReportPeriodButton(
    text: String,
    period: ReportPeriod,
    selectedPeriod: ReportPeriod,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = period == selectedPeriod

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2563EB) else Color.White,
            contentColor = if (isSelected) Color.White else Color(0xFF2563EB)
        ),
        border = if (!isSelected)
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB))
        else null
    ) {
        Text(text)
    }
}

@Composable
fun CategoryInsightsSection(
    top: Pair<String, Double>?,
    worst: Pair<String, Double>?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Category Insights",
            style = MaterialTheme.typography.titleMedium
        )

        if (top == null && worst == null) {
            EmptyStateCard("No category data available")
        } else {
            top?.let {
                InsightCard(
                    "Top Performing",
                    it.first,
                    it.second,
                    Color(0xFFD1FAE5),
                    Color(0xFF065F46)
                )
            }

            worst?.let {
                InsightCard(
                    "Needs Attention",
                    it.first,
                    it.second,
                    Color(0xFFFEE2E2),
                    Color(0xFF991B1B)
                )
            }
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    category: String,
    value: Double,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = textColor)
            Text(category, style = MaterialTheme.typography.titleMedium)
            Text("₱${"%.2f".format(value)}")
        }
    }
}

@Composable
fun ProductInsightsSection(
    best: com.example.smartretailph.viewmodel.ProductInsight?,
    worst: com.example.smartretailph.viewmodel.ProductInsight?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Product Insights",
            style = MaterialTheme.typography.titleMedium
        )

        best?.let {
            ProductInsightCard(
                title = "Best Seller",
                product = it,
                bgColor = Color(0xFFE8F0FF),
                accentColor = Color(0xFF2F6BFF)
            )
        }

        worst?.let {
            ProductInsightCard(
                title = "Underperforming",
                product = it,
                bgColor = Color(0xFFFFF4E5),
                accentColor = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
fun PredictiveSection(
    forecast: Double,
    trend: Double,
    restock: List<Pair<String, Int>>, // (can ignore visually for now)
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 🔹 Title
        Text(
            text = "✨ Predictive Analytics",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔹 NEXT WEEK CARD
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEDE9FE) // soft purple
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Next Week",
                        color = Color(0xFF6D28D9),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "₱${"%.0f".format(forecast * 7)}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Predicted Revenue",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // 🔹 GROWTH CARD
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3E8FF) // lighter purple
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Growth",
                        color = Color(0xFF9333EA),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${if (trend >= 0) "+" else ""}${"%.1f".format(trend)}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (trend >= 0) Color(0xFF16A34A) else Color.Red
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Expected Increase",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProductInsightCard(
    title: String,
    product: com.example.smartretailph.viewmodel.ProductInsight,
    bgColor: Color,
    accentColor: Color
) {

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column {

                    Text(title, color = accentColor)

                    Text(
                        product.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            accentColor,
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${"%.1f".format(product.trendPercent)}%",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                MetricBox("Revenue", "₱${"%.2f".format(product.revenue)}")
                MetricBox("Sales", "${product.sales} units")
                MetricBox("Stock", "${product.stock}")
            }
        }
    }
}

@Composable
fun MetricBox(label: String, value: String) {

    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
