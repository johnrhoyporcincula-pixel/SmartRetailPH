package com.example.smartretailph.data.repositories

import android.content.Context
import com.example.smartretailph.data.database.AppDatabase
import com.example.smartretailph.data.dao.ProductDao
import com.example.smartretailph.data.entities.ProductEntity
import com.example.smartretailph.data.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Simple local inventory repository backed by SharedPreferences.
 * Stores products as a JSON array string.
 */
object InventoryRepository {

    private lateinit var productDao: ProductDao

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        val db = AppDatabase.getInstance(context.applicationContext)
        productDao = db.productDao()

        // Load existing data from Room and populate sample data if needed
        runBlocking {
            withContext(Dispatchers.IO) {
                val entities = productDao.getAll()
                _products.value = entities.map { it.toModel() }

                if (_products.value.isEmpty()) {
                    populateSampleProducts()
                }
            }
        }

        isInitialized = true
    }

    fun addProduct(
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
        runBlocking {
            withContext(Dispatchers.IO) {
                productDao.insert(product.toEntity())
            }
        }

        val updated = _products.value + product
        _products.value = updated
    }

    fun updateProduct(updatedProduct: Product) {
        runBlocking {
            withContext(Dispatchers.IO) {
                productDao.update(updatedProduct.toEntity())
            }
        }

        val updated = _products.value.map { product ->
            if (product.id == updatedProduct.id) updatedProduct else product
        }
        _products.value = updated
    }

    fun deleteProduct(id: String) {
        runBlocking {
            withContext(Dispatchers.IO) {
                productDao.deleteById(id)
            }
        }

        val updated = _products.value.filterNot { it.id == id }
        _products.value = updated
    }

    private fun populateSampleProducts() {

        // Snacks
        addProduct("Piattos Cheese", "SNK-001", 50, 12.0, "Snacks")
        addProduct("Nova Multigrain Chips", "SNK-002", 50, 12.0, "Snacks")
        addProduct("Clover Chips", "SNK-003", 50, 10.0, "Snacks")
        addProduct("Oishi Prawn Crackers", "SNK-004", 50, 10.0, "Snacks")
        addProduct("Boy Bawang Garlic Cornick", "SNK-005", 50, 12.0, "Snacks")
        addProduct("Roller Coaster Chips", "SNK-006", 40, 10.0, "Snacks")
        addProduct("Tortillos BBQ", "SNK-007", 40, 10.0, "Snacks")
        addProduct("Cheezy Corn Crunch", "SNK-008", 40, 10.0, "Snacks")
        addProduct("V-Cut Potato Chips", "SNK-009", 30, 20.0, "Snacks")
        addProduct("Mr Chips", "SNK-010", 30, 8.0, "Snacks")

        // Biscuits
        addProduct("Skyflakes Crackers", "BIS-011", 60, 8.0, "Biscuits")
        addProduct("Fita Crackers", "BIS-012", 50, 8.0, "Biscuits")
        addProduct("Rebisco Crackers", "BIS-013", 50, 7.0, "Biscuits")
        addProduct("Hansel Crackers", "BIS-014", 50, 7.0, "Biscuits")
        addProduct("Cream-O Cookies", "BIS-015", 60, 10.0, "Biscuits")
        addProduct("Oreo Cookies Small", "BIS-016", 40, 12.0, "Biscuits")
        addProduct("Presto Cream Biscuits", "BIS-017", 40, 10.0, "Biscuits")
        addProduct("Choco Mucho Cookie", "BIS-018", 40, 12.0, "Biscuits")
        addProduct("Marie Biscuits", "BIS-019", 40, 8.0, "Biscuits")
        addProduct("Magic Creams", "BIS-020", 40, 10.0, "Biscuits")

        // Instant Noodles
        addProduct("Lucky Me Pancit Canton Original", "NDL-021", 80, 15.0, "Noodles")
        addProduct("Lucky Me Pancit Canton Chili", "NDL-022", 80, 15.0, "Noodles")
        addProduct("Lucky Me Beef Noodles", "NDL-023", 70, 14.0, "Noodles")
        addProduct("Lucky Me Chicken Noodles", "NDL-024", 70, 14.0, "Noodles")
        addProduct("Payless Xtra Big Original", "NDL-025", 50, 18.0, "Noodles")
        addProduct("Nissin Cup Noodles Seafood", "NDL-026", 40, 25.0, "Noodles")
        addProduct("Nissin Ramen Beef", "NDL-027", 40, 18.0, "Noodles")
        addProduct("Quickchow Pancit Canton", "NDL-028", 50, 13.0, "Noodles")

        // Canned Goods
        addProduct("555 Sardines Tomato", "CND-029", 60, 22.0, "Canned Goods")
        addProduct("Mega Sardines", "CND-030", 60, 23.0, "Canned Goods")
        addProduct("Young's Town Sardines", "CND-031", 60, 21.0, "Canned Goods")
        addProduct("Argentina Corned Beef", "CND-032", 40, 45.0, "Canned Goods")
        addProduct("Purefoods Corned Beef", "CND-033", 40, 50.0, "Canned Goods")
        addProduct("CDO Corned Beef", "CND-034", 40, 38.0, "Canned Goods")
        addProduct("Century Tuna Flakes", "CND-035", 50, 35.0, "Canned Goods")
        addProduct("San Marino Tuna", "CND-036", 50, 36.0, "Canned Goods")
        addProduct("Ligo Sardines", "CND-037", 50, 24.0, "Canned Goods")
        addProduct("555 Tuna Afritada", "CND-038", 40, 33.0, "Canned Goods")

        // Coffee
        addProduct("Nescafe Classic Stick", "COF-039", 100, 7.0, "Coffee")
        addProduct("Nescafe 3 in 1 Original", "COF-040", 100, 12.0, "Coffee")
        addProduct("Nescafe Creamy White", "COF-041", 100, 12.0, "Coffee")
        addProduct("Kopiko Brown Coffee", "COF-042", 100, 12.0, "Coffee")
        addProduct("Great Taste White Coffee", "COF-043", 100, 12.0, "Coffee")
        addProduct("San Mig Coffee Mix", "COF-044", 100, 12.0, "Coffee")

        // Powdered Drinks
        addProduct("Milo Sachet", "DRK-045", 80, 10.0, "Drinks")
        addProduct("Ovaltine Sachet", "DRK-046", 60, 10.0, "Drinks")
        addProduct("Tang Orange Sachet", "DRK-047", 60, 12.0, "Drinks")
        addProduct("Nestea Lemon Iced Tea", "DRK-048", 60, 12.0, "Drinks")

        // Beverages
        addProduct("Coca Cola 290ml", "BEV-049", 50, 15.0, "Beverages")
        addProduct("Pepsi 290ml", "BEV-050", 50, 15.0, "Beverages")
        addProduct("Sprite 290ml", "BEV-051", 50, 15.0, "Beverages")
        addProduct("Royal Orange 290ml", "BEV-052", 50, 15.0, "Beverages")
        addProduct("Mountain Dew 290ml", "BEV-053", 50, 15.0, "Beverages")
        addProduct("C2 Green Tea", "BEV-054", 40, 20.0, "Beverages")
        addProduct("Zesto Orange Juice", "BEV-055", 40, 12.0, "Beverages")
        addProduct("Yakult Bottle", "BEV-056", 30, 12.0, "Beverages")

        // Bread & Sweets
        addProduct("Gardenia White Bread Slice", "BRD-057", 20, 75.0, "Bread")
        addProduct("Monay Bread", "BRD-058", 30, 10.0, "Bread")
        addProduct("Ensaymada", "BRD-059", 25, 12.0, "Bread")
        addProduct("Choco Mucho Bar", "SWT-060", 60, 12.0, "Sweets")
        addProduct("Cloud 9 Chocolate", "SWT-061", 60, 10.0, "Sweets")
        addProduct("KitKat Mini", "SWT-062", 40, 15.0, "Sweets")
        addProduct("Hany Chocolate Peanut", "SWT-063", 50, 5.0, "Sweets")
        addProduct("Flat Tops Chocolate", "SWT-064", 50, 5.0, "Sweets")

        // Condiments
        addProduct("Datu Puti Soy Sauce Sachet", "CON-065", 80, 3.0, "Condiments")
        addProduct("Datu Puti Vinegar Sachet", "CON-066", 80, 3.0, "Condiments")
        addProduct("UFC Banana Ketchup Sachet", "CON-067", 80, 4.0, "Condiments")
        addProduct("Mang Tomas All Purpose Sauce", "CON-068", 30, 20.0, "Condiments")
        addProduct("Silver Swan Soy Sauce", "CON-069", 40, 20.0, "Condiments")

        // Seasonings
        addProduct("Magic Sarap Sachet", "SEA-070", 100, 2.0, "Seasoning")
        addProduct("Ajinomoto MSG Sachet", "SEA-071", 100, 2.0, "Seasoning")
        addProduct("Knorr Chicken Cube", "SEA-072", 80, 7.0, "Seasoning")
        addProduct("Knorr Pork Cube", "SEA-073", 80, 7.0, "Seasoning")

        // Toiletries
        addProduct("Safeguard Soap White", "TOI-074", 40, 35.0, "Toiletries")
        addProduct("Palmolive Shampoo Sachet", "TOI-075", 80, 6.0, "Toiletries")
        addProduct("Sunsilk Shampoo Sachet", "TOI-076", 80, 6.0, "Toiletries")
        addProduct("Creamsilk Conditioner Sachet", "TOI-077", 80, 6.0, "Toiletries")
        addProduct("Colgate Toothpaste Small", "TOI-078", 40, 20.0, "Toiletries")
        addProduct("Close Up Toothpaste Small", "TOI-079", 40, 20.0, "Toiletries")
        addProduct("Oral B Toothbrush", "TOI-080", 30, 25.0, "Toiletries")

        // Laundry & Household
        addProduct("Surf Powder Detergent Sachet", "HOM-081", 80, 8.0, "Household")
        addProduct("Tide Powder Sachet", "HOM-082", 80, 8.0, "Household")
        addProduct("Ariel Powder Sachet", "HOM-083", 80, 10.0, "Household")
        addProduct("Champion Detergent Bar", "HOM-084", 40, 20.0, "Household")
        addProduct("Joy Dishwashing Sachet", "HOM-085", 80, 5.0, "Household")
        addProduct("Axion Dishwashing Paste", "HOM-086", 40, 10.0, "Household")

        // Essentials
        addProduct("White Sugar 500g", "ESS-087", 30, 35.0, "Essentials")
        addProduct("Brown Sugar 500g", "ESS-088", 30, 32.0, "Essentials")
        addProduct("Rice 1kg", "ESS-089", 40, 55.0, "Essentials")
        addProduct("Cooking Oil 250ml", "ESS-090", 40, 35.0, "Essentials")
        addProduct("Alaska Evaporated Milk", "ESS-091", 40, 30.0, "Essentials")
        addProduct("Bear Brand Powdered Milk Sachet", "ESS-092", 60, 12.0, "Essentials")

        // Misc
        addProduct("Hope Cigarette Stick", "MSC-093", 100, 8.0, "Cigarettes")
        addProduct("Marlboro Cigarette Stick", "MSC-094", 100, 10.0, "Cigarettes")
        addProduct("Disposable Lighter", "MSC-095", 50, 20.0, "Misc")
        addProduct("Plastic Ice Candy", "MSC-096", 40, 5.0, "Frozen")
        addProduct("Ice Tube", "MSC-097", 40, 3.0, "Frozen")
        addProduct("Mineral Water 500ml", "MSC-098", 50, 15.0, "Beverages")
        addProduct("Mineral Water 1L", "MSC-099", 40, 20.0, "Beverages")
        addProduct("Plastic Bag Small", "MSC-100", 200, 1.0, "Store Supplies")
    }

    private fun Product.toEntity(): ProductEntity =
        ProductEntity(
            id = id,
            name = name,
            sku = sku,
            stockQuantity = stockQuantity,
            category = category,
            price = price
        )

    private fun ProductEntity.toModel(): Product =
        Product(
            id = id,
            name = name,
            sku = sku,
            stockQuantity = stockQuantity,
            category = category,
            price = price
        )
}

