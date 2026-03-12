package com.example.smartretailph.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.smartretailph.data.entities.OrderEntity
import com.example.smartretailph.data.entities.OrderItemEntity
import com.example.smartretailph.data.relations.OrderWithItems

@Dao
interface OrderDao {

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    suspend fun getAllOrdersWithItems(): List<OrderWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteItemsForOrder(orderId: String)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: String)
}
