package com.example.smartretailph

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationTabs_switchContentCorrectly() {
        // Default screen is Dashboard
        composeTestRule.onNodeWithText("Welcome back, User! 👋").assertIsDisplayed()

        // Inventory tab
        composeTestRule.onAllNodesWithText("Inventory")[0].performClick()
        composeTestRule.onNodeWithText("Search products...").assertIsDisplayed()

        // Orders tab
        composeTestRule.onAllNodesWithText("Orders")[0].performClick()
        composeTestRule.onNodeWithText("All Orders").assertIsDisplayed()

        // Reports tab
        composeTestRule.onAllNodesWithText("Reports")[0].performClick()
        composeTestRule.onNodeWithText("Reports & Analytics").assertIsDisplayed()

        // Back to Dashboard
        composeTestRule.onAllNodesWithText("Home")[0].performClick()
        composeTestRule.onNodeWithText("Welcome back, User! 👋").assertIsDisplayed()
    }

    @Test
    fun drawerMenuItems_navigateToCorrectScreens() {
        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        composeTestRule.onNodeWithText("Privacy & Security").performClick()
        composeTestRule.onNodeWithText("Two-Factor Authentication").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Help & Support").performClick()
        composeTestRule.onNodeWithText("FAQ").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("About (v1.0)", substring = true).performClick()
        composeTestRule.onNodeWithText("SmartRetailPH").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Inventory Management").performClick()
        composeTestRule.onNodeWithText("Products").assertIsDisplayed()
    }

    @Test
    fun dashboardQuickActions_doExpectedScreenNavigation() {
        composeTestRule.onNodeWithText("Add Product").performClick()
        composeTestRule.onNodeWithText("Search products...").assertIsDisplayed()

        composeTestRule.onAllNodesWithText("Home")[0].performClick()
        composeTestRule.onNodeWithText("New Sale").performClick()
        composeTestRule.onNodeWithText("All Orders").assertIsDisplayed()

        composeTestRule.onAllNodesWithText("Home")[0].performClick()
        composeTestRule.onNodeWithText("View Reports").performClick()
        composeTestRule.onNodeWithText("Generate Report").performClick()
        composeTestRule.onNodeWithText("Reports & Analytics").assertIsDisplayed()
    }
}
