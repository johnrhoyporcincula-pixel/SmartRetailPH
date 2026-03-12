package com.example.smartretailph.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartretailph.data.dao.OrderDao
import com.example.smartretailph.data.dao.ProductDao
import com.example.smartretailph.data.entities.OrderEntity
import com.example.smartretailph.data.entities.OrderItemEntity
import com.example.smartretailph.data.entities.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_retail_db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
