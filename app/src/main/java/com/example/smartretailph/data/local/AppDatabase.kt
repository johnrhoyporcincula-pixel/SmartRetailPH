package com.example.smartretailph.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartretailph.data.local.dao.OrderDao
import com.example.smartretailph.data.local.dao.ProductDao
import com.example.smartretailph.data.local.dao.ReceiptDao
import com.example.smartretailph.data.local.dao.UserDao
import com.example.smartretailph.data.local.entities.OrderEntity
import com.example.smartretailph.data.local.entities.OrderItemEntity
import com.example.smartretailph.data.local.entities.ReceiptEntity
import com.example.smartretailph.data.local.entities.UserEntity
import com.example.smartretailph.data.models.Product

@Database(
    entities = [
        Product::class,
        OrderEntity::class,
        OrderItemEntity::class,
        ReceiptEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_retail_ph.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

