package com.example.cyberlearnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cyberlearnapp.navigation.Screens
import com.example.cyberlearnapp.navigation.mainGraph
import com.example.cyberlearnapp.ui.components.BottomNavigationBar
import com.example.cyberlearnapp.ui.screens.AuthScreen
import com.example.cyberlearnapp.ui.theme.CyberLearnAppTheme
import com.example.cyberlearnapp.viewmodel.AuthViewModel
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screens.Dashboard.route,
        Screens.Courses.route,
        Screens.Achievements.route,
        Screens.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
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
                        userViewModel.loadUserProgress()
                        navController.navigate(Screens.Main.route) {
                            popUpTo(Screens.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            mainGraph(
                navController = navController,
                authViewModel = authViewModel,
                userViewModel = userViewModel
            )
        }
    }
}