package com.example.smartretailph.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PreferencesScreen() {

    var notifications by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Preferences", style = MaterialTheme.typography.titleLarge)

        Card(shape = RoundedCornerShape(16.dp)) {

            Column {

                PreferenceItem(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    trailing = {
                        Switch(
                            checked = notifications,
                            onCheckedChange = { notifications = it }
                        )
                    }
                )

                PreferenceItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode"
                )

                PreferenceItem(
                    icon = Icons.Default.Language,
                    title = "Language"
                )
            }
        }
    }
}

@Composable
fun PreferenceItem(
    icon: ImageVector,
    title: String,
    trailing: @Composable (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row {

            Icon(icon, null)

            Spacer(Modifier.width(12.dp))

            Text(title)
        }

        trailing?.invoke()
    }
}
