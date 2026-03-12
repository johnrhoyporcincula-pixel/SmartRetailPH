package com.example.smartretailph.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.smartretailph.data.local.entities.OrderEntity
import com.example.smartretailph.data.local.entities.OrderItemEntity
import com.example.smartretailph.data.local.entities.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    fun observeOrdersWithItems(): Flow<List<OrderWithItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: String)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteItemsForOrder(orderId: String)

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun countOrders(): Int
}

