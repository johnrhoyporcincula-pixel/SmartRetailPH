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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DashboardState(
    val totalProducts: Int = 0,
    val totalStockUnits: Int = 0,
    val todaysOrdersCount: Int = 0,
    val totalRevenue: Double = 0.0,  // new
    val lowStock: Int = 0             // new
)

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                InventoryRepository.products,
                OrdersRepository.orders
            ) { products, orders ->

                val calendar = java.util.Calendar.getInstance()
                val todayYear = calendar.get(java.util.Calendar.YEAR)
                val todayMonth = calendar.get(java.util.Calendar.MONTH)
                val todayDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                val todaysOrders = orders.count { order ->
                    val orderDate = java.util.Date(order.createdAtMillis)
                    calendar.time = orderDate
                    val orderYear = calendar.get(java.util.Calendar.YEAR)
                    val orderMonth = calendar.get(java.util.Calendar.MONTH)
                    val orderDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                    orderYear == todayYear && orderMonth == todayMonth && orderDay == todayDay
                }

                val totalRevenue = orders.filter { order ->
                    val orderDate = java.util.Date(order.createdAtMillis)
                    calendar.time = orderDate
                    val orderYear = calendar.get(java.util.Calendar.YEAR)
                    val orderMonth = calendar.get(java.util.Calendar.MONTH)
                    val orderDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                    orderYear == todayYear && orderMonth == todayMonth && orderDay == todayDay
                }.sumOf { it.totalAmount }

                val lowStock = products.count { it.stockQuantity <= 5 }

                DashboardState(
                    totalProducts = products.size,
                    totalStockUnits = products.sumOf { it.stockQuantity },
                    todaysOrdersCount = todaysOrders,
                    totalRevenue = totalRevenue,
                    lowStock = lowStock
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}

