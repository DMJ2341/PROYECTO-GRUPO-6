package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.AuthState
import com.example.cyberlearnapp.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    // Efecto para navegar al éxito
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isRegister) "Crear Cuenta" else "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        if (isRegister) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Spacer(Modifier.height(24.dp))

        if (authState is AuthState.Error) {
            Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (isRegister) viewModel.register(name, email, password)
                else viewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
            else Text(if (isRegister) "REGISTRARSE" else "ENTRAR")
        }

        TextButton(onClick = { isRegister = !isRegister }) {
            Text(if (isRegister) "¿Ya tienes cuenta? Entra aquí" else "¿Nuevo? Regístrate aquí")
        }
    }
}