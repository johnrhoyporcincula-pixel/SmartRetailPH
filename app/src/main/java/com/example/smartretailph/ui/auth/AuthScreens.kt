package com.example.smartretailph.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.smartretailph.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    AuthScaffold(
        title = "Login",
        primaryButtonText = "Login",
        secondaryButtonText = "Create account",
        email = uiState.email,
        password = uiState.password,
        isPasswordVisible = uiState.isPasswordVisible,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onEmailChanged = authViewModel::onEmailChanged,
        onPasswordChanged = authViewModel::onPasswordChanged,
        onTogglePasswordVisibility = authViewModel::onTogglePasswordVisibility,
        onPrimaryClick = { authViewModel.login(onLoginSuccess) },
        onSecondaryClick = onNavigateToSignUp
    )
}

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    AuthScaffold(
        title = "Sign Up",
        primaryButtonText = "Create account",
        secondaryButtonText = "Already have an account?",
        email = uiState.email,
        password = uiState.password,
        isPasswordVisible = uiState.isPasswordVisible,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onEmailChanged = authViewModel::onEmailChanged,
        onPasswordChanged = authViewModel::onPasswordChanged,
        onTogglePasswordVisibility = authViewModel::onTogglePasswordVisibility,
        onPrimaryClick = { authViewModel.signUp(onSignUpSuccess) },
        onSecondaryClick = onNavigateToLogin
    )
}

@Composable
private fun AuthScaffold(
    title: String,
    primaryButtonText: String,
    secondaryButtonText: String,
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChanged,
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChanged,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (isPasswordVisible) "Hide" else "Show",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.clickable { onTogglePasswordVisibility() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = onPrimaryClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.padding(4.dp)
                        )
                    } else {
                        Text(primaryButtonText)
                    }
                }

                TextButton(
                    onClick = onSecondaryClick,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(secondaryButtonText)
                }
            }
        }
    }
}

