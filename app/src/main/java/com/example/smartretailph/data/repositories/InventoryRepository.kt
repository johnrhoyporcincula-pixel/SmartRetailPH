package com.example.smartretailph.data.repositories

import android.content.Context
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.local.dao.ProductDao
import com.example.smartretailph.data.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Inventory repository backed by Room.
 */
object InventoryRepository {

    private lateinit var productDao: ProductDao

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val db = AppDatabase.getInstance(context.applicationContext)
        productDao = db.productDao()

        scope.launch {
            productDao.observeProducts().collect { list ->
                _products.value = list
            }
        }

        scope.launch {
            if (productDao.countProducts() == 0) {
                populateSampleProducts()
            }
        }

        isInitialized = true
    }

    suspend fun addProduct(
        name: String,
        sku: String,
        stockQuantity: Int,
        price: Double,
        category: String = ""
    ) {
        val product = Product(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            sku = sku.trim(),
            stockQuantity = stockQuantity,
            category = category.trim(),
            price = price
        )
        productDao.upsert(product)
    }

    suspend fun updateProduct(updatedProduct: Product) {
        productDao.upsert(updatedProduct)
    }

    suspend fun deleteProduct(id: String) {
        productDao.deleteById(id)
    }

    private fun p(
        name: String,
        sku: String,
        stock: Int,
        price: Double,
        category: String
    ) = Product(
        id = UUID.randomUUID().toString(),
        name = name,
        sku = sku,
        stockQuantity = stock,
        price = price,
        category = category
    )

    private suspend fun populateSampleProducts() {

        val products = listOf(

            p("Piattos Cheese", "SNK-001", 50, 12.0, "Snacks"),
            p("Nova Multigrain Chips", "SNK-002", 50, 12.0, "Snacks"),
            p("Clover Chips", "SNK-003", 50, 10.0, "Snacks")

        )

        productDao.upsertAll(products)
    }
}

