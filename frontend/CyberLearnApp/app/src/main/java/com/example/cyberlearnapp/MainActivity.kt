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

    // 🔍 DEBUG EXTENDIDO
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        println("📍 [ROUTE-DEBUG] Ruta actual: $currentRoute")
        println("👤 [USER-DEBUG] Usuario: ${currentUser?.email ?: "NULL"}")
        println("🚀 [NAV-DEBUG] ShouldNavigateToMain: $shouldNavigateToMain")
        println("⏳ [NAV-DEBUG] isLoading: $isLoading")
    }

    // ✅ Navegación con DEBUG
    LaunchedEffect(currentUser, shouldNavigateToMain) {
        println("🔄 [NAV-TRIGGER] currentUser: ${currentUser != null}, shouldNavigateToMain: $shouldNavigateToMain")

        if ((currentUser != null || shouldNavigateToMain) &&
            currentRoute != Screens.Main.route &&
            !isLoading) {

            println("🎯 [NAV-EXECUTING] Navegando a Main...")
            navController.navigate(Screens.Main.route) {
                launchSingleTop = true
                popUpTo(Screens.Auth.route) { inclusive = true }
            }
            authViewModel.resetNavigation()
        }
    }

    Scaffold(
        bottomBar = {
            // ✅ SOLUCIÓN TEMPORAL: Mostrar siempre para debug
            val showBottomBar = true // currentRoute?.startsWith("main/") == true

            if (showBottomBar) {
                println("📱 [BOTTOMBAR] ✅ MOSTRANDO - Ruta: $currentRoute")
                BottomNavigationBar(navController = navController)
            } else {
                println("📱 [BOTTOMBAR] ❌ OCULTANDO - Ruta: $currentRoute")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        println("✅ [AUTH] Login exitoso, navegando a Main")
                        navController.navigate(Screens.Main.route) {
                            launchSingleTop = true
                            popUpTo(Screens.Auth.route) { inclusive = true }
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