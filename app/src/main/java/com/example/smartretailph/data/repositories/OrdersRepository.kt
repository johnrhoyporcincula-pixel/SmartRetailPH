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

        val customerNames = listOf(
            "Walk-in", "Juan Dela Cruz", "Maria Santos", "Pedro Reyes",
            "Ana Lopez", "Carlo Garcia", "Mark Bautista", "Jenny Cruz",
            "Aling Nena", "Mang Tony"
        )

        val paymentMethods = listOf("Cash", "GCash", "Card")

        val statuses = listOf(
            "Completed", "Completed", "Completed", "Completed", // most orders completed
            "Processing",
            "Pending"
        )

        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        val products = listOf(
            Triple("piattos", "Piattos Cheese", 12.0),
            Triple("nova", "Nova Multigrain Chips", 12.0),
            Triple("skyflakes", "Skyflakes Crackers", 8.0),
            Triple("creamO", "Cream-O Cookies", 10.0),
            Triple("pancitCanton", "Lucky Me Pancit Canton", 15.0),
            Triple("beefNoodles", "Lucky Me Beef Noodles", 14.0),
            Triple("sardines555", "555 Sardines", 22.0),
            Triple("centuryTuna", "Century Tuna", 35.0),
            Triple("nescafe", "Nescafe 3-in-1", 12.0),
            Triple("kopiko", "Kopiko Brown Coffee", 12.0),
            Triple("milo", "Milo Sachet", 10.0),
            Triple("coke", "Coca Cola 290ml", 15.0),
            Triple("sprite", "Sprite 290ml", 15.0),
            Triple("zesto", "Zesto Juice", 12.0),
            Triple("cloud9", "Cloud 9 Chocolate", 10.0),
            Triple("hany", "Hany Chocolate", 5.0),
            Triple("magicSarap", "Magic Sarap", 2.0),
            Triple("soySauce", "Datu Puti Soy Sauce", 3.0),
            Triple("surf", "Surf Detergent Sachet", 8.0),
            Triple("joy", "Joy Dishwashing", 5.0)
        )

        for (day in 0 until 100) {

            val daysAgo = 99 - day
            val baseTime = now - (daysAgo * dayMillis)

            val ordersToday = (3..12).random() // sari store busy day

            repeat(ordersToday) {

                val customer = customerNames.random()
                val payment = paymentMethods.random()
                val status = statuses.random()

                val itemCount = (1..5).random()

                val items = mutableListOf<com.example.smartretailph.data.models.OrderItem>()

                repeat(itemCount) {

                    val product = products.random()

                    val quantity = when (product.first) {
                        "magicSarap", "soySauce" -> (2..5).random()
                        else -> (1..3).random()
                    }

                    items.add(
                        com.example.smartretailph.data.models.OrderItem(
                            productId = product.first,
                            name = product.second,
                            quantity = quantity,
                            unitPrice = product.third
                        )
                    )
                }

                val total = items.sumOf { it.quantity * it.unitPrice }

                val order = Order(
                    id = UUID.randomUUID().toString(),
                    customerName = customer,
                    totalAmount = total,
                    items = items,
                    paymentMethod = payment,
                    status = status,
                    createdAtMillis = baseTime + (Math.random() * dayMillis).toLong()
                )

                val updated = _orders.value + order
                _orders.value = updated
            }
        }

        persist(_orders.value)
    }
}

