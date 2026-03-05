package com.example.smartretailph.util

import android.content.Context
import com.example.smartretailph.data.models.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CSVExporter {
    fun exportOrders(context: Context, orders: List<Order>): Boolean {
        return try {
            val csv = StringBuilder()
            csv.append("Order ID,Customer,Total Amount,Payment Method,Date,Item Count\n")
            val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            orders.sortedByDescending { it.createdAtMillis }.forEach { order ->
                csv.append("\"${order.id}\",")
                csv.append("\"${order.customerName}\",")
                csv.append("%.2f,".format(order.totalAmount))
                csv.append("${order.paymentMethod},")
                csv.append("${dateFmt.format(Date(order.createdAtMillis))},")
                csv.append("${order.items.size}\n")
            }

            val fname = "orders_${System.currentTimeMillis()}.csv"
            context.openFileOutput(fname, Context.MODE_PRIVATE).use { fos ->
                fos.write(csv.toString().toByteArray())
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
