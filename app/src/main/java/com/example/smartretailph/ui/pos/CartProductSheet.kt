package com.example.smartretailph.ui.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartretailph.data.models.Product

@Composable
fun CartProductSheet(
    product: Product,
    onCancel: () -> Unit,
    onAddToCart: (quantity: Int) -> Unit
) {

    var quantity by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable { onCancel() },
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column {

                // HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Point of Sale",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "✕",
                        modifier = Modifier.clickable { onCancel() },
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Divider()

                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // PRODUCT INFO
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Color(0xFFE6EEFF),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛒")
                        }

                        Column {

                            Text(
                                product.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "${product.sku} • ${product.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE6F6EE),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "Stock: ${product.stockQuantity} units",
                                    color = Color(0xFF1BAA5E),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // PRICE BOX
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Color(0xFFD6E2FF),
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                Color(0xFFF3F6FF),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text("Unit Price", color = Color(0xFF3B5CCC))
                                Text(
                                    "₱${"%.2f".format(product.price)}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total", color = Color(0xFF3B5CCC))
                                Text(
                                    "₱${"%.2f".format(product.price * quantity)}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }

                    // QUANTITY
                    Text("Quantity")

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Color(0xFFF0F2F5),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (quantity > 1) quantity--
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-")
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(
                                    1.dp,
                                    Color(0xFFD0D5DD),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                quantity.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Color(0xFF2F6BFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (quantity < product.stockQuantity) quantity++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White)
                        }
                    }

                    Text(
                        "Available: ${product.stockQuantity} units",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // BUTTONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            onClick = onCancel
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            onClick = { onAddToCart(quantity) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2F6BFF)
                            )
                        ) {
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }
    }
}