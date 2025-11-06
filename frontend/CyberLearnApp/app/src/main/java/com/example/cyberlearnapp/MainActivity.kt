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

    // Estado de autenticación
    val currentUser by authViewModel.currentUser.collectAsState()

    // 🔍 DEBUG: Ver estado de autenticación
    LaunchedEffect(currentUser) {
        println("👤 [MAIN-DEBUG] Estado autenticación: ${currentUser?.email ?: "NO AUTENTICADO"}")
    }

    // 🎯 CORRECCIÓN: Start destination basado en autenticación real
    val startDestination = if (currentUser != null) {
        println("🚀 [MAIN-DEBUG] Usuario YA autenticado, yendo a Main")
        Screens.Main.route
    } else {
        println("🚀 [MAIN-DEBUG] Usuario NO autenticado, yendo a Auth")
        Screens.Auth.route
    }

    Scaffold(
        bottomBar = {
            // BottomBar solo se muestra en Main, no en Auth
            if (currentUser != null) {
                BottomNavigationBar(navController = navController)
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
                        println("🎉 [MAIN-DEBUG] Login exitoso! Navegando a Main...")
                        // ✅ CORREGIDO: Solo cargar progreso del usuario
                        // CourseViewModel.loadCourses() se llamará automáticamente desde CoursesScreen
                        userViewModel.loadUserProgress()
                        navController.navigate(Screens.Main.route) {
                            popUpTo(Screens.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            // ✅ CORREGIDO: Pasar TODOS los ViewModels que mainGraph necesita
            mainGraph(
                navController = navController,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                courseViewModel = courseViewModel
            )
        }
    }
}