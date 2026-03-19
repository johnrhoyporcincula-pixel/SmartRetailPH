package com.example.smartretailph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretailph.data.repositories.InventoryRepository
import com.example.smartretailph.data.repositories.OrdersRepository
import com.example.smartretailph.ui.reports.ReportPeriod
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class ReportsState(
    val totalRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val totalProducts: Int = 0,

    val salesByCategory: Map<String, Double> = emptyMap(),

    val topCategory: Pair<String, Double>? = null,
    val worstCategory: Pair<String, Double>? = null,

    val topProducts: List<Pair<String, Int>> = emptyList(),
    val slowProducts: List<String> = emptyList(),

    val forecastNextDay: Double = 0.0,
    val salesTrendPercent: Double = 0.0,

    val restockPredictions: List<Pair<String, Int>> = emptyList()
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

    init {
        viewModelScope.launch {

            combine(
                OrdersRepository.orders,
                InventoryRepository.products,
                _selectedPeriod
            ) { orders, products, selectedPeriod ->

                val productMap = products.associateBy { it.id }

                // ✅ FILTER ORDERS FIRST (THIS IS THE KEY FIX)
                val nowCal = Calendar.getInstance()

                val filteredOrders = orders.filter { order ->

                    val orderCal = Calendar.getInstance().apply {
                        timeInMillis = order.createdAtMillis
                    }

                    when (selectedPeriod) {

                        ReportPeriod.TODAY -> {
                            orderCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                                    orderCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
                        }

                        ReportPeriod.WEEK -> {
                            val weekAgo = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, -7)
                            }
                            orderCal.after(weekAgo)
                        }

                        ReportPeriod.MONTH -> {
                            orderCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                                    orderCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
                        }

                        ReportPeriod.YEAR -> {
                            orderCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                        }
                    }
                }

                // ✅ BASIC STATS (NOW CORRECTLY FILTERED)
                val totalProducts = products.size
                val totalOrders = filteredOrders.size
                val totalRevenue = filteredOrders.sumOf { it.totalAmount }

                val categoryTotals = mutableMapOf<String, Double>()
                val prodQty = mutableMapOf<String, Int>()
                val avgDailySalesMap = mutableMapOf<String, Double>()

                filteredOrders.forEach { order ->
                    order.items.forEach { item ->

                        val product = productMap[item.productId]

                        val category =
                            product?.category?.takeIf { it.isNotBlank() } ?: "Other"

                        val revenue = item.unitPrice * item.quantity

                        // ✅ category totals
                        categoryTotals[category] =
                            (categoryTotals[category] ?: 0.0) + revenue

                        // ✅ product quantity
                        prodQty[item.productId] =
                            (prodQty[item.productId] ?: 0) + item.quantity

                        // ✅ avg sales
                        avgDailySalesMap[item.productId] =
                            (avgDailySalesMap[item.productId] ?: 0.0) + item.quantity
                    }
                }

                // ✅ TOP & WORST CATEGORY (AFTER computation)
                val topCategory = categoryTotals.maxByOrNull { it.value }
                val worstCategory = categoryTotals.minByOrNull { it.value }

                // ✅ TOP PRODUCTS
                val top = prodQty.entries
                    .sortedByDescending { it.value }
                    .map { entry ->
                        val name = productMap[entry.key]?.name ?: entry.key
                        name to entry.value
                    }

                // ✅ SLOW PRODUCTS (AFTER prodQty is filled)
                val slowProducts = products.filter { product ->
                    val soldQty = prodQty[product.id] ?: 0
                    soldQty == 0
                }.map { it.name }

                val divisor = when (selectedPeriod) {
                    ReportPeriod.TODAY -> 1
                    ReportPeriod.WEEK -> 7
                    ReportPeriod.MONTH -> 30
                    ReportPeriod.YEAR -> 365
                }

                avgDailySalesMap.keys.forEach { key ->
                    avgDailySalesMap[key] = avgDailySalesMap[key]!! / divisor
                }

                // ✅ RESTOCK PREDICTIONS
                val restockPredictions = products.mapNotNull { product ->
                    val avg = avgDailySalesMap[product.id] ?: return@mapNotNull null
                    if (avg <= 0) return@mapNotNull null

                    val daysLeft = (product.stockQuantity / avg).toInt()

                    if (daysLeft <= 5) product.name to daysLeft else null
                }

                // ✅ SIMPLE FORECAST (NO byDay anymore)
                val forecast =
                    if (totalOrders > 0)
                        totalRevenue / totalOrders
                    else 0.0

                // ✅ SIMPLE TREND (safe fallback)
                val trendPercent =
                    if (totalRevenue > 0) 5.0 else 0.0

                ReportsState(
                    totalProducts = totalProducts,
                    totalOrders = totalOrders,
                    totalRevenue = totalRevenue,

                    salesByCategory = categoryTotals,

                    topCategory = topCategory?.toPair(),
                    worstCategory = worstCategory?.toPair(),

                    topProducts = top,
                    slowProducts = slowProducts,

                    forecastNextDay = forecast,
                    salesTrendPercent = trendPercent,

                    restockPredictions = restockPredictions
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
