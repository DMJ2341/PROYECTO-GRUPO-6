package com.example.cyberlearnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.cyberlearnapp.navigation.NavGraph
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
                // ✅ ESTO ES LO QUE ARREGLA EL FONDO BLANCO
                // Crea una "hoja" base con el color de fondo de tu tema (PrimaryDark)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}