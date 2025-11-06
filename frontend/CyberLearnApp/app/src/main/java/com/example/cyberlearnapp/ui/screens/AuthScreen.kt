package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cyberlearnapp.ui.theme.*
import com.example.cyberlearnapp.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val shouldNavigateToMain by viewModel.shouldNavigateToMain.collectAsState()

    // ✅ SOLUCIÓN: Navegación controlada únicamente por shouldNavigateToMain
    LaunchedEffect(shouldNavigateToMain) {
        if (shouldNavigateToMain && !isLoading) {
            println("🎯 [AUTH-DEBUG] shouldNavigateToMain detectado, navegando (Loading: $isLoading)")
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PrimaryDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "CyberLearn",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Aprende. Hackea. Protege.",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentCyan,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isRegistering) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre", color = TextGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = AccentCyan,
                                unfocusedLabelColor = TextGray
                            )
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = AccentCyan,
                            unfocusedLabelColor = TextGray
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = AccentCyan,
                            unfocusedLabelColor = TextGray
                        )
                    )

                    // Mensaje de error
                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = Danger,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Botones
                    Button(
                        onClick = {
                            if (isRegistering) {
                                viewModel.register(email, password, name)
                            } else {
                                viewModel.login(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank() &&
                                (!isRegistering || name.isNotBlank()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan,
                            contentColor = TextWhite
                        )
                    ) {
                        // ✅ CRÍTICO: El spinner desaparece ANTES de la navegación
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = TextWhite
                            )
                        } else {
                            Text(if (isRegistering) "Registrarse" else "Iniciar Sesión")
                        }
                    }

                    TextButton(
                        onClick = {
                            isRegistering = !isRegistering
                            viewModel.clearError()
                        }
                    ) {
                        Text(
                            text = if (isRegistering) "¿Ya tienes cuenta? Inicia sesión"
                            else "¿No tienes cuenta? Regístrate",
                            color = AccentCyan
                        )
                    }
                }
            }
        }
    }
}