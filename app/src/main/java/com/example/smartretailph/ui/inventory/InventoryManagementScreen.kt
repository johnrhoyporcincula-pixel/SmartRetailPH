package com.example.smartretailph.ui.inventory

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Report
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartretailph.viewmodel.InventoryViewModel

@Composable
fun InventoryManagementScreen(
    inventoryViewModel: InventoryViewModel = viewModel()
) {

    val products by inventoryViewModel.products.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, true) ||
                it.sku.contains(searchQuery, true)
    }

    val totalItems = products.size
    val lowStock = products.count { it.stockQuantity <= 5 }
    val expiring = 0 // placeholder if you add expiration logic later

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /*
        ---------------------------
        SUMMARY CARDS
        ---------------------------
        */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            InventoryGradientCard(
                title = "Total Items",
                value = totalItems.toString(),
                icon = Icons.Default.Inventory2,
                colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)),
                modifier = Modifier.weight(1f)
            )

            InventoryGradientCard(
                title = "Low Stock",
                value = lowStock.toString(),
                icon = Icons.Default.Warning,
                colors = listOf(Color(0xFFF97316), Color(0xFFFB923C)),
                modifier = Modifier.weight(1f)
            )

            InventoryGradientCard(
                title = "Expiring",
                value = expiring.toString(),
                icon = Icons.Default.Report,
                colors = listOf(Color(0xFF9333EA), Color(0xFFC084FC)),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        /*
        ---------------------------
        SEARCH + ACTIONS
        ---------------------------
        */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                shape = RoundedCornerShape(30.dp),

                placeholder = {
                    Text(
                        text = "Search products or SKU...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },

                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },

                modifier = Modifier.weight(1f),
                singleLine = true
            )

            IconButton(onClick = { }) {
                Icon(Icons.Default.Tune, "Filter")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.LocalOffer, "Category")
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Add,
                        "Add Product",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Showing ${filteredProducts.size} of ${products.size} products",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        /*
        ---------------------------
        PRODUCT LIST
        ---------------------------
        */

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filteredProducts) { product ->

                ProductItemCard(product)

            }
        }
    }
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
                    onClick = { },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit Details")
                }

                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB) // blue like image
                    )
                ) {
                    Text("Update Stock")
                }

                IconButton(
                    onClick = { }
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

@Composable
fun InventoryGradientCard(
    title: String,
    value: String,
    icon: ImageVector,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(colors))
                .padding(16.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {

                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Column {

                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = value,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}