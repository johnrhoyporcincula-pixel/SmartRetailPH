package com.example.smartretailph.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.smartretailph.viewmodel.InventoryViewModel

@Composable
fun CategoryManagementScreen(
    inventoryViewModel: InventoryViewModel
) {

    val products by inventoryViewModel.products.collectAsState()

    var editingCategory by remember { mutableStateOf<String?>(null) }
    var deletingCategory by remember { mutableStateOf<String?>(null) }

    val categories = products.groupBy { it.category.ifBlank { "Uncategorized" } }

    // 👇 MAIN LIST UI
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories.entries.toList()) { entry ->

            Card {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(modifier = Modifier.fillMaxWidth()) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.key, fontWeight = FontWeight.Bold)
                            Text("${entry.value.size} products")
                        }

                        TextButton(onClick = {
                            editingCategory = entry.key
                        }) {
                            Text("Edit")
                        }

                        TextButton(onClick = {
                            deletingCategory = entry.key
                        }) {
                            Text("Delete", color = Color.Red)
                        }
                    }
                }
            }
        }
    }

    editingCategory?.let { categoryName ->

        var newCategoryName by remember { mutableStateOf(categoryName) }

        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Edit Category") },
            text = {
                Column {
                    Text("Rename \"$categoryName\"")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("New Category Name") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            inventoryViewModel.renameCategory(
                                oldCategory = categoryName,
                                newCategory = newCategoryName
                            )
                        }
                        editingCategory = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    deletingCategory?.let { categoryName ->

        var selectedOption by remember { mutableStateOf("UNCATEGORIZED") }
        var targetCategory by remember { mutableStateOf("") }

        val allCategories = products
            .map { it.category }
            .filter { it != categoryName }
            .distinct()

        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = {
                Text(
                    "Delete \"$categoryName\"?",
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Column {

                    Text("What should happen to products under this category?")

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption == "DELETE",
                            onClick = { selectedOption = "DELETE" }
                        )
                        Text("Delete all products")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption == "UNCATEGORIZED",
                            onClick = { selectedOption = "UNCATEGORIZED" }
                        )
                        Text("Move to Uncategorized")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption == "MOVE",
                            onClick = { selectedOption = "MOVE" }
                        )
                        Text("Move to another category")
                    }

                    if (selectedOption == "MOVE") {
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = targetCategory,
                            onValueChange = { targetCategory = it },
                            label = { Text("Target Category") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (selectedOption) {
                            "DELETE" -> {
                                inventoryViewModel.deleteCategoryAndProducts(categoryName)
                            }
                            "UNCATEGORIZED" -> {
                                inventoryViewModel.moveProductsToCategory(
                                    categoryName,
                                    "Uncategorized"
                                )
                            }
                            "MOVE" -> {
                                if (targetCategory.isNotBlank()) {
                                    inventoryViewModel.moveProductsToCategory(
                                        categoryName,
                                        targetCategory
                                    )
                                }
                            }
                        }
                        deletingCategory = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
