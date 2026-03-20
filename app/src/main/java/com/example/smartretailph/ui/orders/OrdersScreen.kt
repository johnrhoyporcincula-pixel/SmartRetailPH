package com.example.smartretailph.ui.orders

import androidx.compose.material.icons.automirrored.filled.Sort
import com.example.smartretailph.data.models.OrderStatus
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
import com.example.smartretailph.data.models.Order
import com.example.smartretailph.viewmodel.OrdersViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel = viewModel()
) {
    var completedOrder by remember { mutableStateOf<Order?>(null) }

    val orders by com.example.smartretailph.data.repositories.OrdersRepository
        .orders
        .collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val receiptsMap by com.example.smartretailph.data.repositories.ReceiptsRepository.receipts.collectAsState()

    val dateFormat = remember {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    }

    var selectedFilter by remember { mutableStateOf("All") }

    val pending = orders.count { it.status == OrderStatus.Pending }
    val processing = orders.count { it.status == OrderStatus.Processing }
    val done = orders.count { it.status == OrderStatus.Completed }

    val filteredOrders = when (selectedFilter) {
        "Pending" -> orders.filter { it.status == OrderStatus.Pending }
        "Processing" -> orders.filter { it.status == OrderStatus.Processing }
        "Completed" -> orders.filter { it.status == OrderStatus.Completed }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(filteredOrders, key = { it.id }) { order ->

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
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

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

                        Spacer(modifier = Modifier.height(12.dp))

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // top accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color)
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 14.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
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
fun StatusPill(status: OrderStatus) {

    val color = when (status) {
        OrderStatus.Pending -> Color(0xFFFFB74D)
        OrderStatus.Processing -> Color(0xFF9575CD)
        OrderStatus.Completed -> Color(0xFF81C784)
        OrderStatus.Cancelled -> Color.Red
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(status.name, color = color)
    }
}

