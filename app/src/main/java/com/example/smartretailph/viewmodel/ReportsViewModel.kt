package com.example.smartretailph.viewmodel

import java.time.Instant
import java.time.ZoneId
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretailph.data.repositories.InventoryRepository
import com.example.smartretailph.data.repositories.OrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReportsState(
    val totalRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val totalProducts: Int = 0,
    val lowStockItems: List<Pair<String, Int>> = emptyList(),
    val salesByDay: Map<String, Double> = emptyMap(),
    val salesByCategory: Map<String, Double> = emptyMap(),
    val topProducts: List<Pair<String, Int>> = emptyList(),
    val forecastNextDay: Double = 0.0
)

class ReportsViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        viewModelScope.launch {

            combine(
                OrdersRepository.orders,
                InventoryRepository.products
            ) { orders, products ->
                // Create a fast lookup map for products
                val productMap = products.associateBy { it.id }

                // TOTAL METRICS
                val totalProducts = products.size
                val totalOrders = orders.size
                val totalRevenue = orders.sumOf { it.totalAmount }

                // SALES BY DAY
                val byDay = mutableMapOf<String, Double>()

                orders.forEach { order ->
                    val key = Instant.ofEpochMilli(order.createdAtMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString()
                    byDay[key] = (byDay[key] ?: 0.0) + order.totalAmount
                }

                // SALES BY CATEGORY  ✅ FIX
                val categoryTotals = mutableMapOf<String, Double>()

                orders.forEach { order ->

                    order.items.forEach { item ->

                        val product = productMap[item.productId]

                        val category =
                            product?.category?.takeIf { it.isNotBlank() } ?: "Other"

                        // Use the price stored in the order
                        val revenue = item.unitPrice * item.quantity

                        categoryTotals[category] =
                            (categoryTotals[category] ?: 0.0) + revenue
                    }
                }

                // TOP PRODUCTS
                val prodQty = mutableMapOf<String, Int>()

                orders.flatMap { it.items }.forEach { item ->
                    prodQty[item.productId] =
                        (prodQty[item.productId] ?: 0) + item.quantity
                }

                val top = prodQty.entries
                    .sortedByDescending { it.value }
                    .map { entry ->

                        val name =
                            productMap[entry.key]?.name ?: entry.key

                        name to entry.value
                    }

                // FORECAST
                val sortedDays = byDay.entries.sortedBy { it.key }
                val last7 = sortedDays.takeLast(7).map { it.value }

                val forecast =
                    if (last7.size >= 2) {
                        val trend = last7.last() - last7.first()
                        last7.average() + (trend / last7.size)
                    } else {
                        last7.firstOrNull() ?: 0.0
                    }

                // LOW STOCK
                val lowStock =
                    products
                        .filter { it.stockQuantity < 10 }
                        .sortedBy { it.stockQuantity }
                        .map { it.name to it.stockQuantity }

                ReportsState(
                    totalProducts = totalProducts,
                    totalOrders = totalOrders,
                    totalRevenue = totalRevenue,
                    salesByDay = byDay,
                    salesByCategory = categoryTotals,   // ✅ FIX ADDED
                    topProducts = top,
                    forecastNextDay = forecast,
                    lowStockItems = lowStock
                )
            }.collect { newState ->

                if (_state.value != newState) {
                    _state.value = newState
                }
            }
        }
    }
}
