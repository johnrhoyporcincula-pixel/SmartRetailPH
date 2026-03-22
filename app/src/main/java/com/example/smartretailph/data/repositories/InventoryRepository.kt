package com.example.smartretailph.data.repositories

import android.content.Context
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.local.dao.ProductDao
import com.example.smartretailph.data.models.Product
import com.example.smartretailph.viewmodel.NotificationsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Inventory repository backed by Room.
 */
object InventoryRepository {
    private lateinit var productDao: ProductDao
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    lateinit var notificationsViewModel: NotificationsViewModel

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isInitialized = false

    fun init(
        context: Context,
        notificationsVM: NotificationsViewModel
    ) {
        if (isInitialized) return

        notificationsViewModel = notificationsVM

        val db = AppDatabase.getInstance(context.applicationContext)
        productDao = db.productDao()

        scope.launch {
            productDao.observeProducts().collect { list ->
                _products.value = list

                // 🔥 REAL-TIME LOW STOCK CHECK
                list.forEach { product ->
                    if (product.stockQuantity <= 5) {
                        notificationsViewModel.notifyLowStock(
                            product.name,
                            product.stockQuantity
                        )
                    }
                }
            }
        }

        scope.launch {
            if (productDao.countProducts() == 0) {
                populateSampleProducts()
            }
        }

        isInitialized = true
    }

    suspend fun addProduct(
        name: String,
        sku: String,
        stockQuantity: Int,
        price: Double,
        category: String = ""
    ) {
        val product = Product(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            sku = sku.trim(),
            stockQuantity = stockQuantity,
            category = category.trim(),
            price = price
        )
        productDao.upsert(product)
    }

    suspend fun updateProduct(updatedProduct: Product) {
        productDao.upsert(updatedProduct)
    }

    suspend fun deleteProduct(id: String) {
        productDao.deleteById(id)
    }

    private fun p(
        name: String,
        sku: String,
        stock: Int,
        price: Double,
        category: String
    ) = Product(
        id = UUID.randomUUID().toString(),
        name = name,
        sku = sku,
        stockQuantity = stock,
        price = price,
        category = category
    )

    fun renameCategory(old: String, new: String) {
        scope.launch {
            val updated = _products.value.map {
                if (it.category == old) it.copy(category = new) else it
            }
            productDao.upsertAll(updated)
        }
    }

    fun deleteCategoryAndProducts(category: String) {
        scope.launch {
            val filtered = _products.value.filter {
                it.category != category
            }
            productDao.upsertAll(filtered)
        }
    }

    fun moveProductsToCategory(old: String, new: String) {
        scope.launch {
            val updated = _products.value.map {
                if (it.category == old) it.copy(category = new) else it
            }
            productDao.upsertAll(updated)
        }
    }

    private suspend fun populateSampleProducts() {

        val products = listOf(

            // Snacks
            p("Piattos Cheese", "SNK-001", 50, 12.0, "Snacks"),
            p("Nova Multigrain Chips", "SNK-002", 50, 12.0, "Snacks"),
            p("Clover Chips", "SNK-003", 50, 10.0, "Snacks"),
            p("Chippy BBQ", "SNK-004", 40, 10.0, "Snacks"),
            p("V-Cut Potato Chips", "SNK-005", 40, 12.0, "Snacks"),
            p("Oishi Prawn Crackers", "SNK-006", 40, 10.0, "Snacks"),
            p("Oishi Marty's Cracklin", "SNK-007", 40, 12.0, "Snacks"),
            p("Boy Bawang Garlic", "SNK-008", 40, 10.0, "Snacks"),
            p("Cracklings", "SNK-009", 40, 10.0, "Snacks"),
            p("Cheezy", "SNK-010", 40, 10.0, "Snacks"),

            // Biscuits
            p("Skyflakes Crackers", "BIS-001", 60, 8.0, "Biscuits"),
            p("Fita Crackers", "BIS-002", 60, 8.0, "Biscuits"),
            p("Hansel Crackers", "BIS-003", 60, 8.0, "Biscuits"),
            p("Rebisco Crackers", "BIS-004", 60, 7.0, "Biscuits"),
            p("Cream-O Chocolate", "BIS-005", 50, 10.0, "Biscuits"),
            p("Oreo Cookies", "BIS-006", 50, 12.0, "Biscuits"),
            p("Presto Peanut Butter", "BIS-007", 50, 10.0, "Biscuits"),
            p("Marie Biscuits", "BIS-008", 50, 8.0, "Biscuits"),
            p("Bingo Chocolate", "BIS-009", 50, 10.0, "Biscuits"),
            p("Magic Creams", "BIS-010", 50, 10.0, "Biscuits"),

            // Instant Noodles
            p("Lucky Me Chicken Noodles", "NDL-001", 80, 15.0, "Instant Noodles"),
            p("Lucky Me Beef Noodles", "NDL-002", 80, 15.0, "Instant Noodles"),
            p("Lucky Me Pancit Canton Original", "NDL-003", 80, 18.0, "Instant Noodles"),
            p("Lucky Me Pancit Canton Chili", "NDL-004", 80, 18.0, "Instant Noodles"),
            p("Lucky Me Pancit Canton Kalamansi", "NDL-005", 80, 18.0, "Instant Noodles"),
            p("Nissin Ramen Seafood", "NDL-006", 60, 20.0, "Instant Noodles"),
            p("Payless Xtra Big", "NDL-007", 60, 20.0, "Instant Noodles"),

            // Coffee
            p("Nescafe Classic Stick", "COF-001", 100, 5.0, "Coffee"),
            p("Nescafe 3-in-1 Original", "COF-002", 100, 12.0, "Coffee"),
            p("Great Taste White Coffee", "COF-003", 100, 12.0, "Coffee"),
            p("Kopiko Brown Coffee", "COF-004", 100, 12.0, "Coffee"),
            p("Kopiko Black Coffee", "COF-005", 100, 12.0, "Coffee"),

            // Powdered Drinks
            p("Milo Sachet", "DRK-001", 80, 12.0, "Drinks"),
            p("Energen Chocolate", "DRK-002", 80, 15.0, "Drinks"),
            p("Tang Orange", "DRK-003", 70, 10.0, "Drinks"),
            p("Tang Grape", "DRK-004", 70, 10.0, "Drinks"),
            p("Nestea Iced Tea", "DRK-005", 70, 10.0, "Drinks"),

            // Canned Goods
            p("Argentina Corned Beef", "CAN-001", 40, 45.0, "Canned Goods"),
            p("Purefoods Corned Beef", "CAN-002", 40, 55.0, "Canned Goods"),
            p("555 Sardines Tomato", "CAN-003", 50, 25.0, "Canned Goods"),
            p("Ligo Sardines", "CAN-004", 50, 25.0, "Canned Goods"),
            p("Mega Sardines", "CAN-005", 50, 25.0, "Canned Goods"),
            p("Century Tuna Flakes", "CAN-006", 40, 35.0, "Canned Goods"),
            p("San Marino Tuna", "CAN-007", 40, 38.0, "Canned Goods"),
            p("Young's Town Sardines", "CAN-008", 50, 24.0, "Canned Goods"),
            p("Highlands Corned Beef", "CAN-009", 40, 55.0, "Canned Goods"),
            p("CDO Meat Loaf", "CAN-010", 40, 30.0, "Canned Goods"),

            // Condiments
            p("Datu Puti Soy Sauce", "CON-001", 30, 12.0, "Condiments"),
            p("Datu Puti Vinegar", "CON-002", 30, 12.0, "Condiments"),
            p("Silver Swan Soy Sauce", "CON-003", 30, 12.0, "Condiments"),
            p("UFC Banana Ketchup", "CON-004", 30, 25.0, "Condiments"),
            p("Mang Tomas All Purpose Sauce", "CON-005", 30, 35.0, "Condiments"),

            // Bread & Spreads
            p("Gardenia White Bread", "BRD-001", 20, 70.0, "Bread"),
            p("Gardenia Wheat Bread", "BRD-002", 20, 75.0, "Bread"),
            p("Lily's Peanut Butter", "SPR-001", 25, 85.0, "Spreads"),
            p("Lady's Choice Mayonnaise", "SPR-002", 25, 95.0, "Spreads"),

            // Candy
            p("Maxx Candy", "CND-001", 200, 1.0, "Candy"),
            p("Snow Bear Candy", "CND-002", 200, 1.0, "Candy"),
            p("Stork Chocolate", "CND-003", 200, 1.0, "Candy"),
            p("Hany Chocolate", "CND-004", 200, 1.0, "Candy"),
            p("Flat Tops Chocolate", "CND-005", 200, 1.0, "Candy"),

            // Beverages
            p("Coca Cola 290ml", "BEV-001", 40, 18.0, "Beverages"),
            p("Pepsi 290ml", "BEV-002", 40, 18.0, "Beverages"),
            p("Sprite 290ml", "BEV-003", 40, 18.0, "Beverages"),
            p("Royal Orange Soda", "BEV-004", 40, 18.0, "Beverages"),
            p("Mountain Dew 290ml", "BEV-005", 40, 18.0, "Beverages"),

            // Water
            p("Wilkins Water 500ml", "WTR-001", 50, 20.0, "Water"),
            p("Absolute Water 500ml", "WTR-002", 50, 20.0, "Water"),
            p("Summit Water 500ml", "WTR-003", 50, 18.0, "Water"),

            // Household Items
            p("Surf Powder Detergent", "HOM-001", 40, 12.0, "Household"),
            p("Tide Powder Detergent", "HOM-002", 40, 12.0, "Household"),
            p("Ariel Powder Detergent", "HOM-003", 40, 12.0, "Household"),
            p("Joy Dishwashing Liquid", "HOM-004", 30, 10.0, "Household"),
            p("Champion Bar Soap", "HOM-005", 30, 15.0, "Household"),

            // Personal Care
            p("Safeguard Soap", "PER-001", 40, 35.0, "Personal Care"),
            p("Palmolive Shampoo Sachet", "PER-002", 80, 6.0, "Personal Care"),
            p("Sunsilk Shampoo Sachet", "PER-003", 80, 6.0, "Personal Care"),
            p("Colgate Toothpaste Small", "PER-004", 50, 25.0, "Personal Care"),
            p("Close Up Toothpaste Small", "PER-005", 50, 25.0, "Personal Care")

        )

        productDao.upsertAll(products)
    }
}
