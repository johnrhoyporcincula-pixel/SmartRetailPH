package com.example.smartretailph.ui.orders

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
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
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    var selectedReceiptId by remember { mutableStateOf<String?>(null) }
    var selectedReceiptText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ===== SHOWCASE CARDS =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OrderStatCard("Total", orders.size, Modifier.weight(1f))
            OrderStatCard("Pending", 0, Modifier.weight(1f))
            OrderStatCard("Processing", 0, Modifier.weight(1f))
            OrderStatCard("Done", orders.size, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== FILTER CHIPS =====
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item { OrderFilterChip("All", orders.size, true) { } }
            item { OrderFilterChip("Pending", 0, false) { } }
            item { OrderFilterChip("Processing", 0, false) { } }
            item { OrderFilterChip("Completed", orders.size, false) { } }

            // Example: add more dynamically if needed
            // item { OrderFilterChip("Refunded", 2, false) { } }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== ORDERS LIST =====
        if (orders.isEmpty()) {
            Text("No orders found.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders, key = { it.id }) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val receipt = receiptsMap[order.id]
                                if (receipt != null) {
                                    selectedReceiptId = order.id
                                    selectedReceiptText = receipt
                                } else {
                                    Toast.makeText(context, "No receipt available", Toast.LENGTH_SHORT).show()
                                }
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            // ---- TOP ROW ----
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ORD-${order.id.takeLast(4)}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Completed",
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ---- BOTTOM ROW ----
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${order.items.size} items · ${dateFormat.format(Date(order.createdAtMillis))}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "₱${"%.2f".format(order.totalAmount)}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ===== RECEIPT DIALOG =====
    if (selectedReceiptId != null) {
        AlertDialog(
            onDismissRequest = {
                selectedReceiptId = null
                selectedReceiptText = ""
            },
            title = { Text("Receipt") },
            text = { Text(selectedReceiptText) },
            confirmButton = {
                TextButton(onClick = {
                    // Fix for clipboard in latest Compose
                    clipboard.setText(AnnotatedString(selectedReceiptText))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedReceiptId = null
                    selectedReceiptText = ""
                }) {
                    Text("Close")
                }
            }
        )
    }
}

// ===== COMPONENTS =====

@Composable
fun OrderStatCard(title: String, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(count.toString(), style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun OrderFilterChip(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text("$label ($count)") }
    )
}