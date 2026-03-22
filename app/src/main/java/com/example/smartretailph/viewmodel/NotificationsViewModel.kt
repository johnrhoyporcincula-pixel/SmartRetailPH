package com.example.smartretailph.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartretailph.ui.notifications.AppNotification
import com.example.smartretailph.ui.notifications.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationsViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun addNotification(notification: AppNotification) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    fun notifyLowStock(productName: String, stock: Int) {

        // Prevent duplicate LOW STOCK notifications for same product
        if (_notifications.value.any {
                it.type == NotificationType.LOW_STOCK &&
                        it.message.contains(productName)
            }) return

        addNotification(
            AppNotification(
                title = "Low Stock Alert",
                message = "$productName is running low ($stock left)",
                type = NotificationType.LOW_STOCK
            )
        )
    }

    fun notifyNewOrder(orderId: String) {
        addNotification(
            AppNotification(
                title = "New Order",
                message = "Order #$orderId has been placed",
                type = NotificationType.NEW_ORDER
            )
        )
    }
}
