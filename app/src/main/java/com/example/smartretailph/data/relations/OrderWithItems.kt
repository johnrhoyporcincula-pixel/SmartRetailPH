package com.example.smartretailph.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartretailph.data.entities.OrderEntity
import com.example.smartretailph.data.entities.OrderItemEntity

data class OrderWithItems(
    @Embedded
    val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)