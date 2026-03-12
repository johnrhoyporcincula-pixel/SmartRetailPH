package com.example.smartretailph.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartretailph.data.local.entities.ReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Query("SELECT * FROM receipts")
    fun observeReceipts(): Flow<List<ReceiptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(receipt: ReceiptEntity)
}

