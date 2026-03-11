package com.example.smartretailph.ui.orders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartretailph.viewmodel.OrdersViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel = viewModel()
) {

    val orders by ordersViewModel.orders.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val receiptsMap by com.example.smartretailph.data.repositories.ReceiptsRepository.receipts.collectAsState()

    val dateFormat = remember {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }

    var selectedFilter by remember { mutableStateOf("All") }

    val pending = orders.count { it.status == "Pending" }
    val processing = orders.count { it.status == "Processing" }
    val done = orders.count { it.status == "Completed" }

    val filteredOrders = when (selectedFilter) {
        "Pending" -> orders.filter { it.status == "Pending" }
        "Processing" -> orders.filter { it.status == "Processing" }
        "Completed" -> orders.filter { it.status == "Completed" }
        else -> orders
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // ================= STATS =================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OrderStatCard(
                "Total",
                orders.size,
                Color(0xFFDDE7FF),
                Modifier.weight(1f)
            )

            OrderStatCard(
                "Pending",
                pending,
                Color(0xFFFFF3CD),
                Modifier.weight(1f)
            )

            OrderStatCard(
                "Processing",
                processing,
                Color(0xFFE6D8FF),
                Modifier.weight(1f)
            )

            OrderStatCard(
                "Done",
                done,
                Color(0xFFDFF5E1),
                Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ================= FILTER CHIPS =================

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            item {
                FilterChipItem("All Orders (${orders.size})", selectedFilter == "All") {
                    selectedFilter = "All"
                }
            }

            item {
                FilterChipItem("Pending ($pending)", selectedFilter == "Pending") {
                    selectedFilter = "Pending"
                }
            }

            item {
                FilterChipItem("Processing ($processing)", selectedFilter == "Processing") {
                    selectedFilter = "Processing"
                }
            }

            item {
                FilterChipItem("Completed ($done)", selectedFilter == "Completed") {
                    selectedFilter = "Completed"
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= ORDER LIST =================

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            items(filteredOrders) { order ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            val receipt = receiptsMap[order.id]

                            if (receipt != null) {
                                clipboard.setText(AnnotatedString(receipt))
                                Toast.makeText(context, "Receipt copied", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "No receipt available", Toast.LENGTH_SHORT).show()
                            }
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(modifier = Modifier.padding(14.dp)) {

                        // top row

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    text = "ORD-${order.id.takeLast(4)}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = order.customerName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            StatusPill(order.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "${order.items.size} items  •  ${dateFormat.format(Date(order.createdAtMillis))}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "₱${"%.2f".format(order.totalAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1B8E3E)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier
                .background(color)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(count.toString(), style = MaterialTheme.typography.titleLarge)

            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}

@Composable
fun StatusPill(status: String) {

    val color = when (status) {
        "Pending" -> Color(0xFFFFB74D)
        "Processing" -> Color(0xFF9575CD)
        "Completed" -> Color(0xFF81C784)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(status, color = color)
    }
}

