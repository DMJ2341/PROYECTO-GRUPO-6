package com.example.cyberlearnapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(16.dp))

        user?.let {
            Text(it.name ?: "Usuario", style = MaterialTheme.typography.headlineSmall)
            Text(it.email, style = MaterialTheme.typography.bodyMedium, color = androidx.compose.ui.graphics.Color.Gray)
        }

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
    }
}