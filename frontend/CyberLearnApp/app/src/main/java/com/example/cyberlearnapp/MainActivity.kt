package com.example.cyberlearnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cyberlearnapp.navigation.NavGraph
import com.example.cyberlearnapp.ui.components.BottomNavigationBar // Necesario
import com.example.cyberlearnapp.ui.theme.CyberLearnAppTheme
import com.example.cyberlearnapp.utils.AuthManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = if (AuthManager.isLoggedIn()) "dashboard" else "auth"

        setContent {
            CyberLearnAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffoldContainer(startDestination)
                }
            }
        }
    }
}

@Composable
fun AppScaffoldContainer(startDestination: String) {
    val navController = rememberNavController()

    // Lógica para determinar la ruta actual y si se debe mostrar la barra
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rutas de nivel superior que deben mostrar la barra de navegación
    val topLevelRoutes = listOf("dashboard", "courses", "glossary", "profile")
    val shouldShowBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        // Aquí se inserta el NavHost (el NavGraph)
        NavGraph(
            navController = navController,
            startDestination = startDestination,
            paddingValues = paddingValues // Pasamos el padding al NavHost
        )
    }
}