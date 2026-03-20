package com.example.smartretailph.ui.main

import androidx.compose.foundation.clickable
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
fun HelpScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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

                FAQItem(
                    question = "What is SmartRetailPH?",
                    answer = "SmartRetailPH is a retail management system that helps you track inventory, monitor sales, and generate reports to improve your business decisions."
                )

                FAQItem(
                    question = "How do I add products?",
                    answer = "Go to the Inventory screen, tap the Add button, and enter product details like name, price, stock quantity, and category."
                )

                FAQItem(
                    question = "How is my inventory updated?",
                    answer = "Inventory is automatically updated whenever a sale is made or when you manually edit stock levels."
                )

                FAQItem(
                    question = "What are predictive analytics?",
                    answer = "Predictive analytics uses your sales data to forecast trends, helping you decide which products to restock or promote."
                )

                FAQItem(
                    question = "Why are my reports empty?",
                    answer = "Reports may be empty if there are no recorded sales within the selected time period (day, week, month, or year). Make sure transactions are being saved correctly."
                )

                FAQItem(
                    question = "How do I change my password?",
                    answer = "Go to Settings > Privacy & Security > Change Password, then enter your current and new password."
                )

                FAQItem(
                    question = "What does Two-Factor Authentication do?",
                    answer = "Two-Factor Authentication adds an extra layer of security by requiring additional verification before accessing your account."
                )

                FAQItem(
                    question = "Is my data محفوظ (safe)?",
                    answer = "Yes. Your data is stored securely and is only accessible to your account. Future updates may include cloud backup and encryption."
                )

                FAQItem(
                    question = "Can I use this app offline?",
                    answer = "Yes. Core features like inventory and sales tracking work offline. Some features may require internet in future updates."
                )

                FAQItem(
                    question = "How do I contact support?",
                    answer = "Go to Help > Contact Support and send us your concern. You can also include screenshots for faster assistance."
                )
            }
        }
    }
}

@Composable
fun FAQItem(
    question: String,
    answer: String
) {
    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded.value = !expanded.value }
            .padding(16.dp)
    ) {

        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium
        )

        if (expanded.value) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Divider()
}
