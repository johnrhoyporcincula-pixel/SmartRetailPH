package com.example.smartretailph.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val sku: String,
    val stockQuantity: Int,
    val category: String,
    val price: Double
)

