package com.example.smartretailph.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.smartretailph.data.models.OrderItem

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<OrderItem>>(emptyList())
    val items: StateFlow<List<OrderItem>> = _items.asStateFlow()

    fun addItem(item: OrderItem) {
        val existing = _items.value.toMutableList()
        // If same product exists, accumulate quantity
        val idx = existing.indexOfFirst { it.productId == item.productId }
        if (idx >= 0) {
            val ex = existing[idx]
            existing[idx] = ex.copy(quantity = ex.quantity + item.quantity)
        } else {
            existing.add(item)
        }
        _items.value = existing
    }

    fun removeItem(productId: String) {
        _items.value = _items.value.filterNot { it.productId == productId }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun totalAmount(): Double = _items.value.sumOf { it.unitPrice * it.quantity }

    fun totalQuantity(): Int = _items.value.sumOf { it.quantity }
}
