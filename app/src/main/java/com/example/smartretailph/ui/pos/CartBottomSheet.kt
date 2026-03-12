package com.example.smartretailph.ui.pos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartretailph.data.models.OrderItem

@Composable
fun CartBottomSheet(
    items: List<OrderItem>,
    onRemove: (String) -> Unit,
    onCheckout: () -> Unit,
    onClose: () -> Unit
) {
    var showCheckoutDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cart", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (items.isEmpty()) {
            Text("Cart is empty", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(items, key = { it.productId }) { it ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            Column {
                                Text(it.name, style = MaterialTheme.typography.titleMedium)
                                Text("Qty: ${it.quantity}", style = MaterialTheme.typography.bodySmall)
                                Text("Unit: $${"%.2f".format(it.unitPrice)}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onRemove(it.productId) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val totalItems = items.sumOf { it.quantity }
            val totalPrice = items.sumOf { it.quantity * it.unitPrice }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clickable { showCheckoutDialog = true },
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF118C5A) // green like the design
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Cart circle
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    shape = MaterialTheme.shapes.extraLarge
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Checkout",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                "$totalItems items",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Text(
                                "Checkout Now",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        "₱${"%.2f".format(totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClose) { Text("Close") }
        }
    }

    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = { Text("Proceed to checkout") },
            text = { Text("Continue to checkout and payment screen.") },
            confirmButton = {
                TextButton(onClick = {
                    showCheckoutDialog = false
                    onCheckout()
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) { Text("Cancel") }
            }
        )
    }
}
