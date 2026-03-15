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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import com.example.smartretailph.viewmodel.InventoryViewModel

enum class InventoryTab {
    PRODUCTS,
    CATEGORIES,
    STOCKS
}

@Composable
fun InventoryManagementScreen(
    inventoryViewModel: InventoryViewModel = viewModel()
) {

    var selectedTab by remember { mutableStateOf(InventoryTab.STOCKS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TabRow(selectedTabIndex = selectedTab.ordinal) {

            Tab(
                selected = selectedTab == InventoryTab.PRODUCTS,
                onClick = { selectedTab = InventoryTab.PRODUCTS },
                text = { Text("Products") }
            )

            Tab(
                selected = selectedTab == InventoryTab.CATEGORIES,
                onClick = { selectedTab = InventoryTab.CATEGORIES },
                text = { Text("Categories") }
            )

            Tab(
                selected = selectedTab == InventoryTab.STOCKS,
                onClick = { selectedTab = InventoryTab.STOCKS },
                text = { Text("Stocks") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {

            InventoryTab.PRODUCTS ->
                ProductManagementScreen(inventoryViewModel)

            InventoryTab.CATEGORIES ->
                CategoryManagementScreen()

            InventoryTab.STOCKS ->
                StockManagementScreen(inventoryViewModel)
        }
    }
}

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
fun ProductManagementScreen(
    inventoryViewModel: InventoryViewModel
) {

    val products by inventoryViewModel.products.collectAsState()

    var editingProduct by remember {
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

                    OutlinedButton(onClick = { editingProduct = product }) {
                        Text("Edit")
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
}

@Composable
fun CategoryManagementScreen(
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val products by inventoryViewModel.products.collectAsState()
    val categories = products.groupBy { it.category.ifBlank { "Uncategorized" } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories.entries.toList()) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = entry.key,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entry.value.size} products",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
private fun AddOrEditProductDialog(
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

