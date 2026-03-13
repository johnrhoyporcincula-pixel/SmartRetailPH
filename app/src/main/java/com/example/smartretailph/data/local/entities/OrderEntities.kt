package com.example.smartretailph.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.smartretailph.data.models.Order
import com.example.smartretailph.data.models.OrderItem
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

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val productId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)

fun OrderWithItems.toDomain(): Order =
    Order(
        id = order.id,
        customerName = order.customerName,
        totalAmount = order.totalAmount,
        items = items.map {
            OrderItem(
                productId = it.productId,
                name = it.name,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        },
        paymentMethod = order.paymentMethod,
        status = order.status,
        createdAtMillis = order.createdAtMillis
    )

