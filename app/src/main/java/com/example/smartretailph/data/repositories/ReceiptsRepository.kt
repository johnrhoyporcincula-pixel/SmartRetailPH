package com.example.smartretailph.data.repositories

import android.content.Context
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.local.dao.ReceiptDao
import com.example.smartretailph.data.local.entities.ReceiptEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ReceiptsRepository {

    private lateinit var receiptDao: ReceiptDao

    private val _receipts = MutableStateFlow<Map<String, String>>(emptyMap())
    val receipts: StateFlow<Map<String, String>> = _receipts.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val db = AppDatabase.getInstance(context.applicationContext)
        receiptDao = db.receiptDao()

        scope.launch {
            receiptDao.observeReceipts().collect { list ->
                _receipts.value = list.associate { it.orderId to it.text }
            }
        }

        isInitialized = true
    }

    suspend fun saveReceipt(orderId: String, receiptText: String) {
        receiptDao.upsert(ReceiptEntity(orderId = orderId, text = receiptText))
    }
}
