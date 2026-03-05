package com.example.smartretailph.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.smartretailph.data.models.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Simple local orders repository backed by SharedPreferences.
 */
object OrdersRepository {

    private const val PREFS_NAME = "orders_prefs"
    private const val KEY_ORDERS = "orders"

    private lateinit var prefs: SharedPreferences

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromStorage()
        // populate sample 100 days of orders when empty
        if (_orders.value.isEmpty()) {
            populateSampleOrders()
        }
        isInitialized = true
    }

    fun addOrder(
        customerName: String,
        totalAmount: Double,
        items: List<com.example.smartretailph.data.models.OrderItem> = emptyList(),
        paymentMethod: String = "Cash",
        createdAtMillis: Long = System.currentTimeMillis()
    ): String {
        val id = UUID.randomUUID().toString()
        val order = Order(
            id = id,
            customerName = customerName.trim(),
            totalAmount = totalAmount,
            items = items,
            paymentMethod = paymentMethod,
            createdAtMillis = createdAtMillis
        )
        val updated = _orders.value + order
        _orders.value = updated
        persist(updated)
        return id
    }

    fun deleteOrder(id: String) {
        val updated = _orders.value.filterNot { it.id == id }
        _orders.value = updated
        persist(updated)
    }

    private fun loadFromStorage() {
        val json = prefs.getString(KEY_ORDERS, null) ?: return
        runCatching {
            val array = JSONArray(json)
            val list = mutableListOf<Order>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Order(
                        id = obj.getString("id"),
                        customerName = obj.getString("customerName"),
                        totalAmount = obj.optDouble("totalAmount", 0.0),
                        items = runCatching {
                            val itemsArray = obj.optJSONArray("items") ?: JSONArray()
                            val tmp = mutableListOf<com.example.smartretailph.data.models.OrderItem>()
                            for (j in 0 until itemsArray.length()) {
                                val it = itemsArray.getJSONObject(j)
                                tmp.add(
                                    com.example.smartretailph.data.models.OrderItem(
                                        productId = it.optString("productId"),
                                        name = it.optString("name"),
                                        quantity = it.optInt("quantity", 1),
                                        unitPrice = it.optDouble("unitPrice", 0.0)
                                    )
                                )
                            }
                            tmp
                        }.getOrElse { emptyList() },
                        paymentMethod = obj.optString("paymentMethod", "Cash"),
                        createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                    )
                )
            }
            _orders.value = list
        }
    }

    private fun persist(orders: List<Order>) {
        val array = JSONArray()
            orders.forEach { order ->
            val obj = JSONObject()
            obj.put("id", order.id)
            obj.put("customerName", order.customerName)
            obj.put("totalAmount", order.totalAmount)
            obj.put("paymentMethod", order.paymentMethod)
            obj.put("createdAtMillis", order.createdAtMillis)

            val itemsArray = JSONArray()
            order.items.forEach { it ->
                val itObj = JSONObject()
                itObj.put("productId", it.productId)
                itObj.put("name", it.name)
                itObj.put("quantity", it.quantity)
                itObj.put("unitPrice", it.unitPrice)
                itemsArray.put(itObj)
            }
            obj.put("items", itemsArray)

            array.put(obj)
        }
        prefs.edit {
            putString(KEY_ORDERS, array.toString())
        }
    }

    private fun populateSampleOrders() {
        val customerNames = listOf("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Henry", "Iris", "Jack", "Walk-in")
        val paymentMethods = listOf("Cash", "Card")
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        // Create orders for the last 100 days
        for (day in 0..99) {
            val daysAgo = 99 - day
            val orderTime = now - (daysAgo * dayInMillis)
            val orderCount = (1..4).random() // 1-4 orders per day

            repeat(orderCount) {
                val customer = customerNames.random()
                val payment = paymentMethods.random()
                val itemCount = (1..3).random()
                val items = mutableListOf<com.example.smartretailph.data.models.OrderItem>()

                // Assign random products to items
                val productIds = listOf("prod-iphone", "prod-samsung", "prod-sony", "prod-logitech", "prod-hp", "prod-notebook", "prod-pen")
                val productNames = listOf("iPhone 14", "Galaxy S23", "Sony Headphones", "Logitech MX3", "HP Envy 13", "Notebook", "Pen")
                val productPrices = listOf(799.99, 749.99, 349.99, 99.99, 999.99, 2.99, 0.99)

                repeat(itemCount) {
                    val idx = productIds.indices.random()
                    items.add(
                        com.example.smartretailph.data.models.OrderItem(
                            productId = productIds[idx],
                            name = productNames[idx],
                            quantity = (1..5).random(),
                            unitPrice = productPrices[idx]
                        )
                    )
                }

                val amount = items.sumOf { it.unitPrice * it.quantity }
                addOrder(customer, amount, items, payment, orderTime + (Math.random() * dayInMillis).toLong())
            }
        }
    }
}

