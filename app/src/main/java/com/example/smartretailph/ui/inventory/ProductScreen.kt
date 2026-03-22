package com.example.smartretailph.ui.inventory

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartretailph.viewmodel.InventoryViewModel

@Composable
fun ProductManagementScreen(
    inventoryViewModel: InventoryViewModel
) {

    val products by inventoryViewModel.products.collectAsState()

    var editingProduct by remember {
        mutableStateOf<com.example.smartretailph.data.models.Product?>(null)
    }

    var deletingProduct by remember {
        mutableStateOf<com.example.smartretailph.data.models.Product?>(null)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(products) { product ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = product.name,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "SKU: ${product.sku}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Category: ${product.category}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row {
                        OutlinedButton(onClick = { editingProduct = product }) {
                            Text("Edit")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { deletingProduct = product }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }
    }

    editingProduct?.let { product ->
        AddOrEditProductDialog(
            title = "Edit Product",
            initial = product,
            onDismiss = { editingProduct = null },
            onConfirm = { name, sku, qty, price, category ->
                inventoryViewModel.updateProduct(
                    product.copy(
                        name = name,
                        sku = sku,
                        stockQuantity = qty,
                        price = price,
                        category = category
                    )
                )
                editingProduct = null
            }
        )
    }

    deletingProduct?.let { product ->

        var confirmationText by remember { mutableStateOf("") }
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { deletingProduct = null },
            title = {
                Text(
                    "Delete ${product.name}?",
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Column {

                    Text(
                        "You are about to permanently delete this product.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "\"${product.name}\" will be removed and cannot be recovered.",
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Type DELETE to confirm",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmationText,
                        onValueChange = { confirmationText = it },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (confirmationText == "DELETE") {
                            inventoryViewModel.deleteProduct(product.id)
                            Toast.makeText(
                                context,
                                "Product deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            deletingProduct = null
                        } else {
                            Toast.makeText(
                                context,
                                "Type DELETE to confirm",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deletingProduct = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddOrEditProductDialog(
    title: String,
    initial: com.example.smartretailph.data.models.Product?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, sku: String, quantity: Int, price: Double, category: String) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var sku by remember { mutableStateOf(initial?.sku.orEmpty()) }
    var category by remember { mutableStateOf(initial?.category.orEmpty()) }
    var quantityText by remember { mutableStateOf(initial?.stockQuantity?.toString().orEmpty()) }
    var priceText by remember { mutableStateOf(initial?.price?.toString().orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quantity") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Price") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val quantity = quantityText.toIntOrNull()
                    val price = priceText.toDoubleOrNull()
                    if (name.isBlank() || sku.isBlank() || quantity == null || price == null) {
                        Toast.makeText(
                            context,
                            "Please fill all fields with valid values",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    onConfirm(name, sku, quantity, price, category)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProductItemCard(
    product: com.example.smartretailph.data.models.Product
) {

    val isLowStock = product.stockQuantity <= 10

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Image placeholder
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Color(0xFFF3F4F6),
                            RoundedCornerShape(12.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "SKU: ${product.sku}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE5E7EB)
                        ) {
                            Text(
                                text = product.category,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                ),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "₱${product.price}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "${product.stockQuantity} in stock",
                            color =
                                if (isLowStock)
                                    Color(0xFFDC2626)   // red
                                else
                                    Color(0xFF16A34A),  // green
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFFE5E7EB))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = { /* Hook where used if needed */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit Details")
                }

                Button(
                    onClick = { /* Hook where used if needed */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Text("Update Stock")
                }

                IconButton(
                    onClick = { /* Hook where used if needed */ }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color(0xFFDC2626)
                    )
                }
            }
        }
    }
}
