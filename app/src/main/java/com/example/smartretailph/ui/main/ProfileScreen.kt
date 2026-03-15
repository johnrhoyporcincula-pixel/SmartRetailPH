package com.example.smartretailph.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun ProfileScreen() {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {

        Card(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFFE5E7EB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text("Alex Thompson", style = MaterialTheme.typography.titleLarge)

                Text(
                    "alex@retailbase.com",
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    Toast
                        .makeText(
                            context,
                            "Profile editing coming soon",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Profile")
                }
            }
        }
    }
}
