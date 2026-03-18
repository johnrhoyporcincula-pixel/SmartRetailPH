package com.example.smartretailph.ui.inventory

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smartretailph.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    inventoryViewModel: InventoryViewModel
) {
    var selectedProduct by remember {
        mutableStateOf<com.example.smartretailph.data.models.Product?>(null)
    }

    var showRestockSheet by remember {
        mutableStateOf(false)
    }

    var showAddDialog by remember { mutableStateOf(false) }

    val products by inventoryViewModel.products.collectAsState()
    val context = LocalContext.current

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

            IconButton(onClick = {
                // Basic UX feedback: clear search so the full list is visible again
                searchQuery = ""
            }) {
                Icon(Icons.Default.Tune, "Filter")
            }

            IconButton(onClick = {
                Toast
                    .makeText(
                        context,
                        "Category filters coming soon",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }) {
                Icon(Icons.Default.LocalOffer, "Category")
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                IconButton(onClick = { showAddDialog = true }) {
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
                StockAlertCard(
                    product = product,
                    onRestockClick = {
                        selectedProduct = product
                        showRestockSheet = true
                    }
                )
            }
        }
    }

    if (showRestockSheet && selectedProduct != null) {

        ModalBottomSheet(
            onDismissRequest = {
                showRestockSheet = false
            }
        ) {

            RestockBottomSheet(
                product = selectedProduct!!,
                inventoryViewModel = inventoryViewModel,
                onClose = {
                    showRestockSheet = false
                }
            )
        }
    }

    if (showAddDialog) {
        AddOrEditProductDialog(
            title = "Add Product",
            initial = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, sku, qty, price, category ->
                if (name.isNotBlank() && sku.isNotBlank() && qty >= 0 && price >= 0.0) {
                    inventoryViewModel.addProduct(
                        name = name,
                        sku = sku,
                        quantity = qty,
                        price = price,
                        category = category
                    )
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RestockBottomSheet(
    product: com.example.smartretailph.data.models.Product,
    inventoryViewModel: InventoryViewModel,
    onClose: () -> Unit
) {

    var quantity by remember { mutableStateOf(1) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        Text(
            text = "Restock Product",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    product.name,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "${product.sku} • ${product.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column {
                        Text("Current Stock", color = Color.Gray)
                        Text(product.stockQuantity.toString(), fontWeight = FontWeight.Bold)
                    }

                    Column {
                        Text("Unit Price", color = Color.Gray)
                        Text("₱${product.price}", color = Color(0xFF16A34A))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Restock Quantity",
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = {
                    if (quantity > 1) quantity--
                }
            ) {
                Text("-")
            }

            Text(
                quantity.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { quantity++ }
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (quantity > 0) {
                        val updated = product.copy(
                            stockQuantity = product.stockQuantity + quantity
                        )
                        inventoryViewModel.updateProduct(updated)
                        Toast.makeText(
                            context,
                            "Restocked ${product.name} by $quantity",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    onClose()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Text("Confirm Restock")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StockAlertCard(
    product: com.example.smartretailph.data.models.Product,
    onRestockClick: () -> Unit
) {

    val threshold = 20
    val isLowStock = product.stockQuantity < threshold

    // if (!isLowStock) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9FAFB)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            /*
            -------------------------
            HEADER
            -------------------------
            */

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color(0xFFFFF4E5),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF97316)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${product.sku} • ${product.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = if (isLowStock) Color(0xFFFFEDD5) else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(50)
                ) {

                    Text(
                        text = if (isLowStock) "Low Stock" else "Normal",
                        color = if (isLowStock) Color(0xFFF97316) else Color(0xFF6B7280),
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            /*
            -------------------------
            STOCK METRICS
            -------------------------
            */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        "Current",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        product.stockQuantity.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        "Threshold",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        threshold.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        "Priority",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        "Medium",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF97316)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /*
            -------------------------
            RESTOCK BUTTON
            -------------------------
            */

            Button(
                onClick = onRestockClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {

                Text("Restock Now")
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

