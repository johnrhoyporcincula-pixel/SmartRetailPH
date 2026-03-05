package com.example.smartretailph.ui.pos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.smartretailph.data.models.Product
import androidx.compose.foundation.clickable

@Composable
fun CartProductSheet(
    product: Product, // your Product model
    onCancel: () -> Unit,
    onAddToCart: (quantity: Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)) // dim background
            .clickable { onCancel() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f), // almost entire screen
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Top Tab: Blue square + name + stock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Blue)
                    )
                    Column {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Text("Stock: ${product.stockQuantity}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Unit price & total price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Unit: ₱${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Total: ₱${"%.2f".format(product.price * quantity)}", style = MaterialTheme.typography.bodyMedium)
                }

                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
                    Text(quantity.toString(), style = MaterialTheme.typography.titleMedium)
                    Button(onClick = { if (quantity < product.stockQuantity) quantity++ }) { Text("+") }
                }

                Spacer(modifier = Modifier.weight(1f)) // push buttons to bottom

                // Bottom buttons: Cancel / Add to Cart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onCancel() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { onAddToCart(quantity) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Add to Cart") }
                }
            }
        }
    }
}