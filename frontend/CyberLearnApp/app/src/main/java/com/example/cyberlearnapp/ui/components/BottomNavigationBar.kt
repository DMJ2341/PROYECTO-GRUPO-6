package com.example.cyberlearnapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cyberlearnapp.navigation.Screens

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Triple("Inicio", Screens.Dashboard.route, Icons.Default.Home),
        Triple("Cursos", Screens.Courses.route, Icons.Default.School),
        Triple("Logros", Screens.Achievements.route, Icons.Default.EmojiEvents),
        Triple("Perfil", Screens.Profile.route, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { (label, route, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screens.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}