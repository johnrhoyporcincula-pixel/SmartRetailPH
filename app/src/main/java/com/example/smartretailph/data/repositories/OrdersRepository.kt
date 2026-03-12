package com.example.smartretailph.data.repositories

import android.content.Context
import com.example.smartretailph.data.database.AppDatabase
import com.example.smartretailph.data.dao.OrderDao
import com.example.smartretailph.data.entities.OrderEntity
import com.example.smartretailph.data.entities.OrderItemEntity
import com.example.smartretailph.data.models.Order
import com.example.smartretailph.data.models.OrderItem
import com.example.smartretailph.data.models.OrderStatus
import com.example.smartretailph.data.models.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

object OrdersRepository {
    private var orderCounter = 1

    private lateinit var orderDao: OrderDao

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val db = AppDatabase.getInstance(context.applicationContext)
        orderDao = db.orderDao()

        // Load existing orders from Room and seed sample data if needed
        runBlocking {
            withContext(Dispatchers.IO) {
                val fromDb = orderDao.getAllOrdersWithItems()
                val list = fromDb.map { rel ->
                    Order(
                        id = rel.order.id,
                        customerName = rel.order.customerName,
                        totalAmount = rel.order.totalAmount,
                        items = rel.items.map {
                            OrderItem(
                                productId = it.productId,
                                name = it.name,
                                quantity = it.quantity,
                                unitPrice = it.unitPrice
                            )
                        },
                        paymentMethod = rel.order.paymentMethod,
                        status = rel.order.status,
                        createdAtMillis = rel.order.createdAtMillis
                    )
                }
                _orders.value = list

                if (_orders.value.isEmpty()) {
                    populateSampleOrders()
                }
            }
        }

        isInitialized = true
    }

    fun generateOrderNumber(): String {
        val number = orderCounter++
        return "ORD-" + number.toString().padStart(4, '0')
    }

    fun addOrder(
        customerName: String,
        totalAmount: Double,
        items: List<OrderItem> = emptyList(),
        paymentMethod: PaymentMethod = PaymentMethod.Cash,
        status: OrderStatus = OrderStatus.Completed,
        createdAtMillis: Long = System.currentTimeMillis()
    ): String {

        val id = UUID.randomUUID().toString()

        val order = Order(
            id = id,
            customerName = customerName.trim(),
            totalAmount = totalAmount,
            items = items,
            paymentMethod = paymentMethod,
            status = status,
            createdAtMillis = createdAtMillis
        )

        // Persist to Room on IO thread but keep this API synchronous
        runBlocking {
            withContext(Dispatchers.IO) {
                orderDao.insertOrder(
                    OrderEntity(
                        id = order.id,
                        customerName = order.customerName,
                        totalAmount = order.totalAmount,
                        paymentMethod = order.paymentMethod,
                        status = order.status,
                        createdAtMillis = order.createdAtMillis
                    )
                )

                if (order.items.isNotEmpty()) {
                    val itemEntities = order.items.map {
                        OrderItemEntity(
                            orderId = order.id,
                            productId = it.productId,
                            name = it.name,
                            quantity = it.quantity,
                            unitPrice = it.unitPrice
                        )
                    }
                    orderDao.insertItems(itemEntities)
                }
            }
        }

        val updated = _orders.value + order
        _orders.value = updated

        return id
    }

    fun deleteOrder(id: String) {
        runBlocking {
            withContext(Dispatchers.IO) {
                orderDao.deleteItemsForOrder(id)
                orderDao.deleteOrderById(id)
            }
        }

        val updated = _orders.value.filterNot { it.id == id }
        _orders.value = updated
    }

    private fun populateSampleOrders() {

        val customerNames = listOf(
            "Walk-in", "Juan Dela Cruz", "Maria Santos",
            "Pedro Reyes", "Ana Lopez", "Carlo Garcia",
            "Mark Bautista", "Jenny Cruz", "Aling Nena",
            "Mang Tony"
        )

        val paymentMethods = listOf(
            PaymentMethod.Cash,
            PaymentMethod.GCash,
            PaymentMethod.Card
        )

        val statuses = listOf(
            OrderStatus.Completed,
            OrderStatus.Completed,
            OrderStatus.Completed,
            OrderStatus.Completed,
            OrderStatus.Processing,
            OrderStatus.Pending
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
            Triple("kopiko", "Kopiko Brown Coffee", 12.0)
        )

        for (day in 0 until 100) {

            val daysAgo = 99 - day
            val baseTime = now - (daysAgo * dayMillis)

            val ordersToday = (3..12).random()

            repeat(ordersToday) {

                val customer = customerNames.random()
                val payment = paymentMethods.random()
                val status = statuses.random()

                val itemCount = (1..5).random()

                val items = mutableListOf<OrderItem>()

                repeat(itemCount) {

                    val product = products.random()

                    val quantity =
                        if (product.first == "magicSarap") (2..5).random()
                        else (1..3).random()

                    items.add(
                        OrderItem(
                            productId = product.first,
                            name = product.second,
                            quantity = quantity,
                            unitPrice = product.third
                        )
                    )
                }

                val total = items.sumOf { it.quantity * it.unitPrice }

                val order = Order(
                    id = OrdersRepository.generateOrderNumber(),
                    customerName = customer,
                    totalAmount = total,
                    items = items,
                    paymentMethod = payment,
                    status = status,
                    createdAtMillis = baseTime + (Math.random() * dayMillis).toLong()
                )

                _orders.value = _orders.value + order
            }
        }

        persist(_orders.value)
    }
}
