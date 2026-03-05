package com.example.smartretailph.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.smartretailph.data.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Simple local inventory repository backed by SharedPreferences.
 * Stores products as a JSON array string.
 */
object InventoryRepository {

    private const val PREFS_NAME = "inventory_prefs"
    private const val KEY_PRODUCTS = "products"

    private lateinit var prefs: SharedPreferences

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromStorage()
        // populate sample data when empty (helpful for testing/demo)
        if (_products.value.isEmpty()) {
            populateSampleProducts()
        }
        isInitialized = true
    }

    fun addProduct(
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
        val updated = _products.value + product
        _products.value = updated
        persist(updated)
    }

    fun updateProduct(updatedProduct: Product) {
        val updated = _products.value.map { product ->
            if (product.id == updatedProduct.id) updatedProduct else product
        }
        _products.value = updated
        persist(updated)
    }

    fun deleteProduct(id: String) {
        val updated = _products.value.filterNot { it.id == id }
        _products.value = updated
        persist(updated)
    }

    private fun loadFromStorage() {
        val json = prefs.getString(KEY_PRODUCTS, null) ?: return
        runCatching {
            val array = JSONArray(json)
            val list = mutableListOf<Product>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Product(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        sku = obj.getString("sku"),
                        stockQuantity = obj.getInt("stockQuantity"),
                        category = obj.optString("category", ""),
                        price = obj.getDouble("price")
                    )
                )
            }
            _products.value = list
        }
    }

    private fun persist(products: List<Product>) {
        val array = JSONArray()
        products.forEach { product ->
            val obj = JSONObject()
            obj.put("id", product.id)
            obj.put("name", product.name)
            obj.put("sku", product.sku)
            obj.put("stockQuantity", product.stockQuantity)
            obj.put("category", product.category)
            obj.put("price", product.price)
            array.put(obj)
        }
        prefs.edit {
            putString(KEY_PRODUCTS, array.toString())
        }
    }

    private fun populateSampleProducts() {
        addProduct("Apple iPhone 14", "IP14-001", 10, 799.99, "Electronics")
        addProduct("Samsung Galaxy S23", "SGS23-001", 8, 749.99, "Electronics")
        addProduct("Sony WH-1000XM5 Headphones", "SONY-HM5", 15, 349.99, "Audio")
        addProduct("Logitech MX Master 3", "LOG-MX3", 20, 99.99, "Accessories")
        addProduct("HP Envy 13 Laptop", "HP-ENVY13", 5, 999.99, "Computers")
        addProduct("Notebook - A5", "NB-A5-001", 100, 2.99, "Stationery")
        addProduct("Ballpoint Pen - Blue", "PEN-BL-01", 200, 0.99, "Stationery")
    }
}

