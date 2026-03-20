package com.example.smartretailph.ui.receipt

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartretailph.data.models.Order
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString

@Composable
fun ReceiptScreen(
    order: Order,
    onDone: () -> Unit
) {
    val df = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    "RECEIPT",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(8.dp))
                Divider()

                Text("SmartRetailPH")
                Text("Date: ${df.format(Date(order.createdAtMillis))}")
                Text("Order #: ${order.id.take(6)}")

                Spacer(Modifier.height(8.dp))
                Divider()

                Row(Modifier.fillMaxWidth()) {
                    Text("Item", Modifier.weight(1f))
                    Text("Qty")
                    Spacer(Modifier.width(8.dp))
                    Text("Price")
                }

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .heightIn(max = 250.dp) // 👈 LIMIT HEIGHT
                        .verticalScroll(scrollState)
                ) {
                    order.items.forEach {
                        Row(Modifier.fillMaxWidth()) {
                            Text(it.name, Modifier.weight(1f))
                            Text("${it.quantity}")
                            Spacer(Modifier.width(8.dp))
                            Text("₱${"%.2f".format(it.unitPrice)}")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Divider()

                Row(Modifier.fillMaxWidth()) {
                    Text("TOTAL", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("₱${"%.2f".format(order.totalAmount)}", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "THANK YOU!",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(16.dp))

                val clipboard = LocalClipboardManager.current
                val context = LocalContext.current

                Column {
                    Button(
                        onClick = {
                            clipboard.setText(
                                AnnotatedString(
                                    com.example.smartretailph.util.ReceiptGenerator.generate(order)
                                )
                            )
                            Toast.makeText(context, "Receipt copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Copy Receipt")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
