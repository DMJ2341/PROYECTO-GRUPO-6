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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val authSuccess by authViewModel.authSuccess.collectAsState()

    // 🔍 DEBUG
    LaunchedEffect(currentUser) {
        println("👤 [MAIN-DEBUG] Usuario: ${currentUser?.email ?: "NULL"}")
    }

    // ✅ Navegación por auth exitosa
    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            println("🚀 [MAIN-DEBUG] Navegando a Main por authSuccess")
            navController.navigate(Screens.Main.route) {
                popUpTo(Screens.Auth.route) { inclusive = true }
            }
            authViewModel.resetAuthSuccess()
        }
    }

    // ✅ Navegación inicial
    var initialNavDone by remember { mutableStateOf(false) }
    LaunchedEffect(currentUser) {
        if (!initialNavDone) {
            if (currentUser != null) {
                println("🚀 [MAIN-DEBUG] Navegación inicial a Main")
                navController.navigate(Screens.Main.route) {
                    popUpTo(0)
                }
            }
            initialNavDone = true
        }
    }

    val startDestination = if (currentUser != null && initialNavDone) Screens.Main.route else Screens.Auth.route

    Scaffold(
        bottomBar = {
            // ✅ CORREGIDO: Mostrar BottomBar en las rutas principales
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