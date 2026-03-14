package com.example.smartretailph.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Help & Support",
            style = MaterialTheme.typography.titleLarge
        )

        Card(shape = RoundedCornerShape(16.dp)) {

            Column {

                HelpItem(
                    icon = Icons.Default.Help,
                    title = "FAQ"
                )

                HelpItem(
                    icon = Icons.Default.SupportAgent,
                    title = "Contact Support"
                )
            }
        }
    }
}

@Composable
fun HelpItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Icon(icon, contentDescription = null)

        Spacer(modifier = Modifier.width(12.dp))

        Text(title)
    }
}