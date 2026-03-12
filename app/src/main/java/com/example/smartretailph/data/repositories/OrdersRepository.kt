package com.example.smartretailph.data.repositories

import android.content.Context
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.local.dao.OrderDao
import com.example.smartretailph.data.local.entities.OrderEntity
import com.example.smartretailph.data.local.entities.OrderItemEntity
import com.example.smartretailph.data.local.entities.toDomain
import com.example.smartretailph.data.models.Order
import com.example.smartretailph.data.models.OrderItem
import com.example.smartretailph.data.models.OrderStatus
import com.example.smartretailph.data.models.PaymentMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

object OrdersRepository {
    private var orderCounter = 1

    private lateinit var orderDao: OrderDao

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val db = AppDatabase.getInstance(context.applicationContext)
        orderDao = db.orderDao()

        scope.launch {
            orderDao.observeOrdersWithItems().collect { list ->
                _orders.value = list.map { it.toDomain() }
            }
        }

        scope.launch {
            if (orderDao.countOrders() == 0) {
                populateSampleOrders()
            }
        }

        isInitialized = true
    }

    fun generateOrderNumber(): String {
        val number = orderCounter++
        return "ORD-" + number.toString().padStart(4, '0')
    }

    suspend fun addOrder(
        customerName: String,
        totalAmount: Double,
        items: List<OrderItem> = emptyList(),
        paymentMethod: PaymentMethod = PaymentMethod.Cash,
        status: OrderStatus = OrderStatus.Completed,
        createdAtMillis: Long = System.currentTimeMillis()
    ): Order {

        val id = UUID.randomUUID().toString()

        val orderEntity = OrderEntity(
            id = id,
            customerName = customerName.trim(),
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            status = status,
            createdAtMillis = createdAtMillis
        )

        val itemEntities = items.map {
            OrderItemEntity(
                orderId = id,
                productId = it.productId,
                name = it.name,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        }

        orderDao.insertOrder(orderEntity)
        if (itemEntities.isNotEmpty()) {
            orderDao.insertItems(itemEntities)
        }

        return Order(
            id = id,
            customerName = orderEntity.customerName,
            totalAmount = orderEntity.totalAmount,
            items = items,
            paymentMethod = orderEntity.paymentMethod,
            status = orderEntity.status,
            createdAtMillis = orderEntity.createdAtMillis
        )
    }

    suspend fun deleteOrder(id: String) {
        orderDao.deleteItemsForOrder(id)
        orderDao.deleteOrderById(id)
    }

    private suspend fun populateSampleOrders() {

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

                addOrder(
                    customerName = customer,
                    totalAmount = total,
                    items = items,
                    paymentMethod = payment,
                    status = status,
                    createdAtMillis = baseTime + (Math.random() * dayMillis).toLong()
                )
            }
        }
    }
}