package com.example.smartretailph.viewmodel

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
    val salesByCategory: Map<String, Double> = emptyMap(),  // <- dynamic
    val topProducts: List<Pair<String, Int>> = emptyList(),
    val forecastNextDay: Double = 0.0
)

class ReportsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        viewModelScope.launch {
            combine(OrdersRepository.orders, InventoryRepository.products) { orders, products ->
                // total metrics
                val totalProducts = products.size
                val totalOrders = orders.size
                val totalRevenue = orders.sumOf { it.totalAmount }

                // sales by day
                val byDay = mutableMapOf<String, Double>()
                orders.forEach { o ->
                    val key = dateFmt.format(Date(o.createdAtMillis))
                    byDay[key] = (byDay[key] ?: 0.0) + o.totalAmount
                }

                // top products
                val prodQty = mutableMapOf<String, Int>()
                orders.flatMap { it.items }.forEach { it ->
                    prodQty[it.productId] = (prodQty[it.productId] ?: 0) + it.quantity
                }
                val top = prodQty.entries.sortedByDescending { it.value }.map { entry ->
                    val name = products.find { it.id == entry.key }?.name ?: entry.key
                    name to entry.value
                }

                // simple forecast: average of last 7 days (or available days)
                val sortedDays = byDay.entries.sortedBy { it.key }
                val last7 = sortedDays.takeLast(7).map { it.value }
                val forecast = if (last7.isNotEmpty()) last7.average() else 0.0

                // low stock items (< 10 units)
                val lowStock = products.filter { it.stockQuantity < 10 }.map { it.name to it.stockQuantity }

                ReportsState(
                    totalProducts = totalProducts,
                    totalOrders = totalOrders,
                    totalRevenue = totalRevenue,
                    salesByDay = byDay.toMap(),
                    topProducts = top,
                    forecastNextDay = forecast,
                    lowStockItems = lowStock
                )
            }.collect { st -> _state.value = st }
        }
    }
}

