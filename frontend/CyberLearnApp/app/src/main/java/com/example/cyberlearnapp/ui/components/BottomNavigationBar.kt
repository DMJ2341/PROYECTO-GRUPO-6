package com.example.cyberlearnapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cyberlearnapp.navigation.Screens
import com.example.cyberlearnapp.ui.theme.*

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = CardBg,
        tonalElevation = 8.dp
    ) {
        // ========== INICIO ==========
        NavigationBarItem(
            icon = { Icon("🏠", contentDescription = "Inicio") },
            label = { Text("Inicio") },
            // CORREGIDO: Usar Screens.Dashboard.route
            selected = currentRoute == Screens.Dashboard.route,
            onClick = {
                // CORREGIDO: Navegar a Screens.Dashboard.route
                if (currentRoute != Screens.Dashboard.route) {
                    navController.navigate(Screens.Dashboard.route) {
                        // Pop hasta el inicio del grafo 'main' para evitar pila infinita
                        popUpTo(Screens.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = AccentCyan,
                selectedTextColor = AccentCyan,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = CardBg
            )
        )

        // ========== CURSOS ==========
        NavigationBarItem(
            icon = { Icon("📚", contentDescription = "Cursos") },
            label = { Text("Cursos") },
            // CORREGIDO: Usar Screens.Courses.route
            selected = currentRoute == Screens.Courses.route,
            onClick = {
                if (currentRoute != Screens.Courses.route) {
                    navController.navigate(Screens.Courses.route) {
                        popUpTo(Screens.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = AccentCyan,
                selectedTextColor = AccentCyan,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = CardBg
            )
        )

        // ========== LOGROS ==========
        NavigationBarItem(
            icon = { Icon("🏆", contentDescription = "Logros") },
            label = { Text("Logros") },
            // CORREGIDO: Usar Screens.Achievements.route
            selected = currentRoute == Screens.Achievements.route,
            onClick = {
                if (currentRoute != Screens.Achievements.route) {
                    navController.navigate(Screens.Achievements.route) {
                        popUpTo(Screens.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = AccentCyan,
                selectedTextColor = AccentCyan,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = CardBg
            )
        )

        // ========== PERFIL ==========
        NavigationBarItem(
            icon = { Icon("👤", contentDescription = "Perfil") },
            label = { Text("Perfil") },
            // CORREGIDO: Usar Screens.Profile.route
            selected = currentRoute == Screens.Profile.route,
            onClick = {
                if (currentRoute != Screens.Profile.route) {
                    navController.navigate(Screens.Profile.route) {
                        popUpTo(Screens.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = AccentCyan,
                selectedTextColor = AccentCyan,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = CardBg
            )
        )
    }
}

// Componente Icon simple para emojis
@Composable
fun Icon(emoji: String, contentDescription: String) {
    Text(text = emoji)
}