package com.example.smartretailph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.repositories.InventoryRepository
import com.example.smartretailph.data.repositories.OrdersRepository
import com.example.smartretailph.data.repositories.ReceiptsRepository
import com.example.smartretailph.ui.main.MainScaffold
import com.example.smartretailph.ui.theme.SmartRetailPHTheme
import com.example.smartretailph.viewmodel.NotificationsViewModel

class MainActivity : ComponentActivity() {

    // Single shared instance tied to Activity lifecycle
    private val notificationsViewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.getInstance(applicationContext)

        // ✅ PASS ViewModel to repositories that need it
        InventoryRepository.init(applicationContext, notificationsViewModel)
        OrdersRepository.init(applicationContext)
        ReceiptsRepository.init(applicationContext)

        setContent {
            SmartRetailPHTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScaffold(
                        notificationsVM = notificationsViewModel, // ✅ SAME INSTANCE
                        onLogout = {
                            // Placeholder
                        }
                    )
                }
            }
        }
    }
}