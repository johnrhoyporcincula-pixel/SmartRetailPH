package com.example.smartretailph.data.models

data class OrderItem(
    val productId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
)
