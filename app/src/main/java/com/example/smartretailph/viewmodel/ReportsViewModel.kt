package com.example.smartretailph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretailph.data.repositories.InventoryRepository
import com.example.smartretailph.data.repositories.OrdersRepository
import com.example.smartretailph.ui.reports.ReportPeriod
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    // ✅ PERIOD STATE
    private val _selectedPeriod = MutableStateFlow(ReportPeriod.TODAY)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()

    fun setPeriod(period: ReportPeriod) {
        _selectedPeriod.value = period
    }

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        viewModelScope.launch {

            combine(
                OrdersRepository.orders,
                InventoryRepository.products,
                _selectedPeriod
            ) { orders, products, selectedPeriod ->

                val productMap = products.associateBy { it.id }
                val nowCal = Calendar.getInstance()

                // ✅ FILTER ORDERS FIRST (THIS IS THE KEY FIX)
                val filteredOrders = orders.filter { order ->
                    val cal = Calendar.getInstance().apply {
                        time = Date(order.createdAtMillis)
                    }

                    when (selectedPeriod) {

                        ReportPeriod.TODAY -> {
                            cal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                                    cal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
                        }

                        ReportPeriod.WEEK -> {
                            val weekAgo = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, -7)
                            }
                            cal.after(weekAgo)
                        }

                        ReportPeriod.MONTH -> {
                            cal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                                    cal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
                        }

                        ReportPeriod.YEAR -> {
                            cal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                        }
                    }
                }

                // ✅ BASIC STATS (NOW CORRECTLY FILTERED)
                val totalProducts = products.size
                val totalOrders = filteredOrders.size
                val totalRevenue = filteredOrders.sumOf { it.totalAmount }

                // ✅ SALES BY DAY (FILTERED)
                val byDay = mutableMapOf<String, Double>()

                filteredOrders.forEach { order ->
                    val key = dateFmt.format(Date(order.createdAtMillis))
                    byDay[key] = (byDay[key] ?: 0.0) + order.totalAmount
                }

                // ✅ CATEGORY SALES (FILTERED)
                val categoryTotals = mutableMapOf<String, Double>()

                filteredOrders.forEach { order ->
                    order.items.forEach { item ->

                        val product = productMap[item.productId]

                        val category =
                            product?.category?.takeIf { it.isNotBlank() } ?: "Other"

                        val revenue = item.unitPrice * item.quantity

                        categoryTotals[category] =
                            (categoryTotals[category] ?: 0.0) + revenue
                    }
                }

                // ✅ TOP PRODUCTS (FILTERED)
                val prodQty = mutableMapOf<String, Int>()

                filteredOrders.flatMap { it.items }.forEach { item ->
                    prodQty[item.productId] =
                        (prodQty[item.productId] ?: 0) + item.quantity
                }

                val top = prodQty.entries
                    .sortedByDescending { it.value }
                    .map { entry ->
                        val name = productMap[entry.key]?.name ?: entry.key
                        name to entry.value
                    }

                // ✅ FORECAST (BASED ON FILTERED DATA)
                val sortedDays = byDay.entries.sortedBy { it.key }
                val last7 = sortedDays.takeLast(7).map { it.value }

                val forecast =
                    if (last7.size >= 2) {
                        val trend = last7.last() - last7.first()
                        last7.average() + (trend / last7.size)
                    } else {
                        last7.firstOrNull() ?: 0.0
                    }

                // ✅ LOW STOCK (NOT FILTERED — CORRECT)
                val lowStock =
                    products
                        .filter { it.stockQuantity < 10 }
                        .sortedBy { it.stockQuantity }
                        .map { it.name to it.stockQuantity }

                ReportsState(
                    totalProducts = totalProducts,
                    totalOrders = totalOrders,
                    totalRevenue = totalRevenue,
                    salesByDay = byDay, // already filtered
                    salesByCategory = categoryTotals,
                    topProducts = top,
                    forecastNextDay = forecast,
                    lowStockItems = lowStock
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
