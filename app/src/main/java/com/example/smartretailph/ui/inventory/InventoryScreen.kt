package com.example.smartretailph.ui.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartretailph.ui.pos.CartProductSheet
import com.example.smartretailph.viewmodel.InventoryViewModel
import com.example.smartretailph.viewmodel.OrdersViewModel
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val products by inventoryViewModel.products.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter products by category
    val filteredProducts = products.filter { product ->
        val matchesCategory =
            selectedCategory == null || product.category == selectedCategory

        val matchesSearch =
            searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.sku.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    // Get unique categories
    val categories = products.map { it.category }.distinct().sorted()

    // POS sell dialog state
    var sellProduct by remember { mutableStateOf<com.example.smartretailph.data.models.Product?>(null) }

    val ordersViewModel: OrdersViewModel = viewModel()
    val cartViewModel: com.example.smartretailph.viewmodel.CartViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text("Search products…") },
            singleLine = true
        )

        // Category filter chips
        if (categories.isNotEmpty()) {
            Text("Filter by Category", style = MaterialTheme.typography.titleSmall)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All") }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (filteredProducts.isEmpty()) {
            Text(
                if (selectedCategory == null)
                    "No products available."
                else
                    "No products in this category.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            // 3-column grid layout
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onProductClick = {
                            // Set the clicked product to show CartProductSheet
                            sellProduct = product
                        },
                        onDelete = { inventoryViewModel.deleteProduct(product.id) }
                    )
                }
            }
        }
    }

    if (sellProduct != null) {
        CartProductSheet(
            product = sellProduct!!,
            onCancel = { sellProduct = null }, // dismiss the sheet
            onAddToCart = { qty ->
                // Add the selected quantity to the cart
                val item = com.example.smartretailph.data.models.OrderItem(
                    productId = sellProduct!!.id,
                    name = sellProduct!!.name,
                    quantity = qty,
                    unitPrice = sellProduct!!.price
                )
                cartViewModel.addItem(item)

                // Close the bottom sheet
                sellProduct = null
            }
        )
    }

    // Floating cart button and cart overlay
    val cartItems by cartViewModel.items.collectAsState()
    var showCart by remember { mutableStateOf(false) }

    // Checkout dialog state
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var checkoutCustomerName by remember { mutableStateOf("Walk-in") }
    var checkoutPaymentMethod by remember { mutableStateOf("Cash") }
    var pendingCartItems by remember { mutableStateOf<List<com.example.smartretailph.data.models.OrderItem>>(emptyList()) }
    var pendingCartTotal by remember { mutableStateOf(0.0) }

    val infiniteTransition = rememberInfiniteTransition(label = "cartPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    if (cartItems.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            androidx.compose.material3.FloatingActionButton(
                onClick = { showCart = true },
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        bottom = 88.dp // clears bottom navigation bar
                    )
                    .scale(pulseScale)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(cartViewModel.totalQuantity().toString())
                }
            }
        }
    }

    if (showCart) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { showCart = false }
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                com.example.smartretailph.ui.pos.CartBottomSheet(
                    items = cartItems,
                    onRemove = { pid -> cartViewModel.removeItem(pid) },
                    onClose = { showCart = false },
                    onCheckout = {
                        // open checkout confirmation dialog
                        showCart = false
                        pendingCartItems = cartItems
                        pendingCartTotal = cartViewModel.totalAmount()
                        checkoutCustomerName = "Walk-in"
                        checkoutPaymentMethod = "Cash"
                        showCheckoutDialog = true
                    }
                )
            }
        }
    }

    if (showCheckoutDialog && pendingCartItems.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = { Text("Checkout") },
            text = {
                Column {
                    OutlinedTextField(
                        value = checkoutCustomerName,
                        onValueChange = { checkoutCustomerName = it },
                        label = { Text("Customer name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Payment: ", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { checkoutPaymentMethod = "Cash" }) { Text("Cash") }
                        TextButton(onClick = { checkoutPaymentMethod = "Card" }) { Text("Card") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total: $${"%.2f".format(pendingCartTotal)}", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // create order
                    val orderId = ordersViewModel.addOrder(
                        if (checkoutCustomerName.isBlank()) "Walk-in" else checkoutCustomerName,
                        pendingCartTotal,
                        pendingCartItems,
                        checkoutPaymentMethod
                    )
                    // Deduct stock
                    pendingCartItems.forEach { ci ->
                        val prod = products.find { it.id == ci.productId }
                        if (prod != null) {
                            val updated = prod.copy(stockQuantity = prod.stockQuantity - ci.quantity)
                            inventoryViewModel.updateProduct(updated)
                        }
                    }
                    // Generate and save receipt
                    val createdOrder = com.example.smartretailph.data.repositories.OrdersRepository.orders.value.find { it.id == orderId }
                    if (createdOrder != null) {
                        val receipt = com.example.smartretailph.util.ReceiptGenerator.generate(createdOrder)
                        com.example.smartretailph.data.repositories.ReceiptsRepository.saveReceipt(orderId, receipt)
                    }
                    cartViewModel.clear()
                    showCheckoutDialog = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) { Text("Cancel") }
            }
        )
    }

}

@Composable
fun ProductCard(
    product: com.example.smartretailph.data.models.Product,
    onProductClick: () -> Unit,
    onDelete: () -> Unit // kept to avoid breaking callers
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // Product name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            // Product price
            Text(
                text = "₱${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
