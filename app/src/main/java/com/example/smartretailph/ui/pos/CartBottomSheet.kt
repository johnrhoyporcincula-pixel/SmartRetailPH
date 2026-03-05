package com.example.smartretailph.ui.pos

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { showCheckoutDialog = true }) { Text("Checkout") }
                Text("Items: ${items.sumOf { it.quantity }}", style = MaterialTheme.typography.bodyMedium)
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
