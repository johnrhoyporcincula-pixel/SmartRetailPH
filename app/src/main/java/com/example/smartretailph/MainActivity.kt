package com.example.smartretailph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.repositories.InventoryRepository
import com.example.smartretailph.data.repositories.OrdersRepository
import com.example.smartretailph.ui.main.MainScaffold
import com.example.smartretailph.ui.theme.SmartRetailPHTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database and local repositories
        AppDatabase.getInstance(applicationContext)
        InventoryRepository.init(applicationContext)
        OrdersRepository.init(applicationContext)
        com.example.smartretailph.data.repositories.ReceiptsRepository.init(applicationContext)

        setContent {
            SmartRetailPHTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScaffold(
                        onLogout = {
                            // No account system; this can later be repurposed
                            // to confirm and close the app if desired.
                        }
                    )
                }
            }
        }
    }
}