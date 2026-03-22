package com.example.smartretailph.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartretailph.viewmodel.NotificationsViewModel
import java.util.UUID

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType
)

enum class NotificationType {
    LOW_STOCK,
    NEW_ORDER,
    INFO
}

@Composable
fun NotificationsOverlay(
    viewModel: NotificationsViewModel,
    onDismiss: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 🔹 Background (ONLY this is clickable)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable { onDismiss() }
        )

        // 🔹 Popup (NOT clickable outside)
        NotificationsPopup(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 56.dp, end = 16.dp)
                .clickable(enabled = false) {},
            onDismiss = onDismiss
        )
    }
}

@Composable
fun NotificationsPopup(
    viewModel: NotificationsViewModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val notifications = viewModel.notifications.collectAsState()

    Card(
        modifier = modifier
            .width(340.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {

        Column {

            Text(
                text = "Notifications",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp)
            )

            Divider()

            if (notifications.value.isEmpty()) {
                Text(
                    text = "No notifications",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                notifications.value.forEach { notif ->

                    val (icon, color, bg) = when (notif.type) {
                        NotificationType.LOW_STOCK -> Triple(
                            Icons.Default.Warning,
                            Color(0xFFF59E0B),
                            Color(0xFFFFF3CD)
                        )

                        NotificationType.NEW_ORDER -> Triple(
                            Icons.Default.ShoppingCart,
                            Color(0xFF16A34A),
                            Color(0xFFE6F7EC)
                        )

                        else -> Triple(
                            Icons.Default.Notifications,
                            Color(0xFF2563EB),
                            Color(0xFFE8F0FE)
                        )
                    }

                    NotificationItem(
                        icon = icon,
                        iconColor = color,
                        iconBg = bg,
                        title = notif.title,
                        message = notif.message,
                        time = "Just now"
                    )

                    Divider()
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    message: String,
    time: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Text(
                text = message,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
