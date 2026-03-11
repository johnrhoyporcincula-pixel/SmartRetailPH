package com.example.smartretailph.data.models

data class Order(
    val id: String,
    val customerName: String,
    val totalAmount: Double,
    val items: List<OrderItem> = emptyList(),
    val paymentMethod: String = "Cash",
    val status: String = "Completed", // ADD THIS
    val createdAtMillis: Long
)

