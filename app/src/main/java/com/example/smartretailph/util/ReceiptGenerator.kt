package com.example.smartretailph.util

import com.example.smartretailph.data.models.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptGenerator {
    fun generate(order: Order): String {
        val df = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("SmartRetailPH\n")
        sb.append("Order: ${order.id}\n")
        sb.append("Customer: ${order.customerName}\n")
        sb.append("Date: ${df.format(Date(order.createdAtMillis))}\n")
        sb.append("--------------------------------\n")
        order.items.forEach { it ->
            sb.append("${it.name} x${it.quantity} @ ${"%.2f".format(it.unitPrice)} = ${"%.2f".format(it.unitPrice * it.quantity)}\n")
        }
        sb.append("--------------------------------\n")
        sb.append("Total: $${"%.2f".format(order.totalAmount)}\n")
        sb.append("Payment: ${order.paymentMethod}\n")
        sb.append("Thank you for your purchase!\n")
        return sb.toString()
    }
}
