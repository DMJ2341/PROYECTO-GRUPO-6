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

        // ✅ CORRECCIÓN AQUÍ: Usamos Screens.PreferenceTest.route (que vale "preference_test")
        // NO uses el string "test" porque esa ruta no existe en tu NavGraph.
        Triple("Test", Screens.PreferenceTest.route, Icons.Default.Assignment), // Usa Assignment si Psychology falla

        Triple("Glosario", Screens.Glossary.route, Icons.Default.MenuBook),
        Triple("Perfil", Screens.Profile.route, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Opcional: Mostrar barra también si estamos en resultados del test
    val showBottomBar = items.any { it.second == currentRoute } || currentRoute == "test_result"

    if (showBottomBar) {
        NavigationBar {
            items.forEach { (label, route, icon) ->
                // Lógica para resaltar el botón si estamos en el test o en sus resultados
                val isSelected = currentRoute == route ||
                        (route == Screens.PreferenceTest.route && currentRoute == "test_result")

                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screens.Dashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}