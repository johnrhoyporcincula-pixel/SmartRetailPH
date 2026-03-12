package com.example.smartretailph.data.local

import androidx.room.TypeConverter
import com.example.smartretailph.data.models.OrderStatus
import com.example.smartretailph.data.models.PaymentMethod

class Converters {

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus?): String? = value?.name

    @TypeConverter
    fun toOrderStatus(value: String?): OrderStatus? =
        value?.let { runCatching { OrderStatus.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? =
        value?.let { runCatching { PaymentMethod.valueOf(it) }.getOrNull() }
}

