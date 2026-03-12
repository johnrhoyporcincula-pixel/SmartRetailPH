package com.example.smartretailph.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val sku: String,
    val stockQuantity: Int,
    val category: String,
    val price: Double
)