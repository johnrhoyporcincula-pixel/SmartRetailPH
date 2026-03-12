package com.example.smartretailph.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey
    val orderId: String,
    val text: String
)

