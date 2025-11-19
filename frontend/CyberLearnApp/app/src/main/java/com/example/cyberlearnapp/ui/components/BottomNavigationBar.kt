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
        NavigationBarItem(
            icon = { Icon("🏠", contentDescription = "Inicio") },
            label = { Text("Inicio") },
            // ✅ CORREGIDO: Usar ruta completa
            selected = currentRoute == "main/dashboard",
            onClick = {
                navController.navigate("main/dashboard") {
                    launchSingleTop = true
                    restoreState = true
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

        NavigationBarItem(
            icon = { Icon("📚", contentDescription = "Cursos") },
            label = { Text("Cursos") },
            // ✅ CORREGIDO: Usar ruta completa
            selected = currentRoute == "main/courses",
            onClick = {
                navController.navigate("main/courses") {
                    launchSingleTop = true
                    restoreState = true
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

        NavigationBarItem(
            icon = { Icon("🏆", contentDescription = "Logros") },
            label = { Text("Logros") },
            // ✅ CORREGIDO: Usar ruta completa
            selected = currentRoute == "main/achievements",
            onClick = {
                navController.navigate("main/achievements") {
                    launchSingleTop = true
                    restoreState = true
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

        NavigationBarItem(
            icon = { Icon("👤", contentDescription = "Perfil") },
            label = { Text("Perfil") },
            // ✅ CORREGIDO: Usar ruta completa
            selected = currentRoute == "main/profile",
            onClick = {
                navController.navigate("main/profile") {
                    launchSingleTop = true
                    restoreState = true
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