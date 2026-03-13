package com.example.smartretailph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretailph.data.models.Order
import com.example.smartretailph.data.models.PaymentMethod
import com.example.smartretailph.data.repositories.OrdersRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    val orders: StateFlow<List<Order>> =
        OrdersRepository.orders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun addOrder(
        customerName: String,
        totalAmount: Double,
        items: List<com.example.smartretailph.data.models.OrderItem> = emptyList(),
        paymentMethod: PaymentMethod
    ): Order {
        return OrdersRepository.addOrder(customerName, totalAmount, items, paymentMethod)
    }

    fun deleteOrder(id: String) {
        viewModelScope.launch {
            OrdersRepository.deleteOrder(id)
        }
    }
}
