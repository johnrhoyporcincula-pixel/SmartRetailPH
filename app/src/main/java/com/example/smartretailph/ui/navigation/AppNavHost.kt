package com.example.smartretailph.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartretailph.ui.auth.LoginScreen
import com.example.smartretailph.ui.auth.SignUpScreen
import com.example.smartretailph.ui.main.MainScaffold
import com.example.smartretailph.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = if (currentUser == null) AuthRoutes.LOGIN else MainRoutes.ROOT

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(AuthRoutes.SIGN_UP)
                },
                onLoginSuccess = {
                    navController.navigate(MainRoutes.ROOT) {
                        popUpTo(AuthRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AuthRoutes.SIGN_UP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(MainRoutes.ROOT) {
                        popUpTo(AuthRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(MainRoutes.ROOT) {
            if (currentUser == null) {
                navController.navigate(AuthRoutes.LOGIN) {
                    popUpTo(MainRoutes.ROOT) { inclusive = true }
                }
            } else {
                MainScaffold(
                    onLogout = {
                        authViewModel.logout {
                            navController.navigate(AuthRoutes.LOGIN) {
                                popUpTo(MainRoutes.ROOT) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

