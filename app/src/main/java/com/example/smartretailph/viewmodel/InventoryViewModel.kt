package com.example.smartretailph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretailph.data.models.Product
import com.example.smartretailph.data.repositories.InventoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    val products: StateFlow<List<Product>> =
        InventoryRepository.products.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(name: String, sku: String, quantity: Int, price: Double, category: String = "") {
        viewModelScope.launch {
            InventoryRepository.addProduct(name, sku, quantity, price, category)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            InventoryRepository.updateProduct(product)
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            InventoryRepository.deleteProduct(id)
        }
    }
}

