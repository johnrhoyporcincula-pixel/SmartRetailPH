package com.example.smartretailph.data.models

enum class OrderStatus {
    Pending,
    Processing,
    Completed,
    Cancelled
}

enum class PaymentMethod {
    Cash,
    Card,
    GCash,
    PayMaya
}

data class Order(
    val id: String,
    val customerName: String,
    val totalAmount: Double,
    val items: List<OrderItem> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.Cash,
    val status: OrderStatus = OrderStatus.Completed,
    val createdAtMillis: Long
)
