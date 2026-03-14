package com.example.smartretailph.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SecurityScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Privacy & Security",
            style = MaterialTheme.typography.titleLarge
        )

        Card(shape = RoundedCornerShape(16.dp)) {

            Column {

                SecurityItem(
                    icon = Icons.Default.Password,
                    title = "Change Password"
                )

                SecurityItem(
                    icon = Icons.Default.Lock,
                    title = "Two-Factor Authentication"
                )
            }
        }
    }
}

@Composable
fun SecurityItem(
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