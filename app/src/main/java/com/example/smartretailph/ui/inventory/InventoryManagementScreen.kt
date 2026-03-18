package com.example.smartretailph.ui.inventory

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Report
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import com.example.smartretailph.viewmodel.InventoryViewModel

enum class InventoryTab {
    PRODUCTS,
    CATEGORIES,
    STOCKS
}

@Composable
fun InventoryManagementScreen(
    inventoryViewModel: InventoryViewModel = viewModel()
) {

    var selectedTab by remember { mutableStateOf(InventoryTab.STOCKS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TabRow(selectedTabIndex = selectedTab.ordinal) {

            Tab(
                selected = selectedTab == InventoryTab.PRODUCTS,
                onClick = { selectedTab = InventoryTab.PRODUCTS },
                text = { Text("Products") }
            )

            Tab(
                selected = selectedTab == InventoryTab.CATEGORIES,
                onClick = { selectedTab = InventoryTab.CATEGORIES },
                text = { Text("Categories") }
            )

            Tab(
                selected = selectedTab == InventoryTab.STOCKS,
                onClick = { selectedTab = InventoryTab.STOCKS },
                text = { Text("Stocks") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {

            InventoryTab.PRODUCTS ->
                ProductManagementScreen(inventoryViewModel)

            InventoryTab.CATEGORIES ->
                CategoryManagementScreen(inventoryViewModel)

            InventoryTab.STOCKS ->
                StockManagementScreen(inventoryViewModel)
        }
    }
}
