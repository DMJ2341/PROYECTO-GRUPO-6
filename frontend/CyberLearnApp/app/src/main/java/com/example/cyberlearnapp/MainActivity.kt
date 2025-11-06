package com.example.cyberlearnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cyberlearnapp.navigation.Screens
import com.example.cyberlearnapp.navigation.mainGraph
import com.example.cyberlearnapp.ui.components.BottomNavigationBar
import com.example.cyberlearnapp.ui.screens.AuthScreen
import com.example.cyberlearnapp.ui.theme.CyberLearnAppTheme
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.CourseViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberLearnAppTheme {
                CyberLearnApp()
            }
        }
    }
}

@Composable
fun CyberLearnApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val courseViewModel: CourseViewModel = hiltViewModel()

    val currentUser by authViewModel.currentUser.collectAsState()
    val shouldNavigateToMain by authViewModel.shouldNavigateToMain.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // 🔍 DEBUG mejorado
    LaunchedEffect(currentUser) {
        println("👤 [MAIN-DEBUG] Usuario: ${currentUser?.email ?: "NULL"}")
    }

    // ✅ SOLUCIÓN: Navegación controlada únicamente por shouldNavigateToMain
    LaunchedEffect(shouldNavigateToMain) {
        if (shouldNavigateToMain && !isLoading) {
            println("🚀 [MAIN-DEBUG] Navegando a Main por shouldNavigateToMain (Loading: $isLoading)")

            navController.navigate(Screens.Main.route) {
                launchSingleTop = true
            }

            authViewModel.resetNavigation()
        }
    }

    // ✅ SOLUCIÓN: Eliminar navegación inicial automática - solo navegar cuando hay usuario REAL
    LaunchedEffect(currentUser) {
        // Solo navegar si hay un usuario REAL (no null) y no estamos ya en Main
        if (currentUser != null &&
            navController.currentBackStackEntry?.destination?.route != Screens.Main.route &&
            !shouldNavigateToMain) {
            println("🚀 [MAIN-DEBUG] Navegando a Main para usuario existente: ${currentUser?.email}")
            navController.navigate(Screens.Main.route) {
                launchSingleTop = true
            }
        }
    }

    // ✅ SOLUCIÓN: Start destination siempre Auth al inicio
    val startDestination = Screens.Auth.route

    Scaffold(
        bottomBar = {
            // Solo mostrar BottomBar si hay usuario y estamos en rutas principales
            if (currentUser != null) {
                val currentRoute = navController.currentDestination?.route
                val showBottomBar = currentRoute in listOf(
                    Screens.Dashboard.route,
                    Screens.Courses.route,
                    Screens.Achievements.route,
                    Screens.Profile.route
                )

                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        println("✅ [MAIN-DEBUG] onLoginSuccess llamado")
                        navController.navigate(Screens.Main.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            mainGraph(
                navController = navController,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                courseViewModel = courseViewModel
            )
        }
    }
}