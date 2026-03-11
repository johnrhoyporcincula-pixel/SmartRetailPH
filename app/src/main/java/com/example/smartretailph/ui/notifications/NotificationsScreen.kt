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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationsOverlay(
    onDismiss: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable { onDismiss() }
    ) {

        NotificationsPopup(
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
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {

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

            NotificationItem(
                icon = Icons.Default.Warning,
                iconColor = Color(0xFFF59E0B),
                iconBg = Color(0xFFFFF3CD),
                title = "Low Stock Alert",
                message = "Whole Milk 1L is running low (12 units left)",
                time = "2 hours ago"
            )

            Divider()

            NotificationItem(
                icon = Icons.Default.Notifications,
                iconColor = Color(0xFF2563EB),
                iconBg = Color(0xFFE8F0FE),
                title = "New Order Received",
                message = "Order #ORD-1234 has been placed",
                time = "4 hours ago"
            )

            Divider()

            NotificationItem(
                icon = Icons.Default.ShoppingCart,
                iconColor = Color(0xFF16A34A),
                iconBg = Color(0xFFE6F7EC),
                title = "Product Added",
                message = "Coca-Cola 500ml added to inventory",
                time = "6 hours ago"
            )

            Divider()

            Text(
                text = "View All Notifications",
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
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
