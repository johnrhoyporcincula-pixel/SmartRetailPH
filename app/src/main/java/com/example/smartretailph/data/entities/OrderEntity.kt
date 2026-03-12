package com.example.smartretailph.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartretailph.data.models.OrderStatus
import com.example.smartretailph.data.models.PaymentMethod

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val customerName: String,
    val totalAmount: Double,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val createdAtMillis: Long
)