package com.example.smartretailph.data.models

data class Product(
    val id: String,
    val name: String,
    val sku: String,
    val stockQuantity: Int,
    val category: String = "",
    val price: Double
)

