package com.example.cyberlearnapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.CourseViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "auth"
) {
    // ViewModels compartidos o instanciados aquí si es necesario
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- AUTH ---
        composable("auth") {
            // ✅ CORREGIDO: Usamos onLoginSuccess en lugar de pasar navController
            AuthScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // Al loguearse, vamos al dashboard y limpiamos el historial para no volver al login con "atrás"
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // --- DASHBOARD ---
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        // --- LISTA DE CURSOS ---
        composable("courses") {
            val courseViewModel: CourseViewModel = hiltViewModel()
            CoursesScreen(
                navController = navController,
                viewModel = courseViewModel
            )
        }

        // --- DETALLE DEL CURSO ---
        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            val courseViewModel: CourseViewModel = hiltViewModel()

            CourseDetailScreen(
                navController = navController,
                viewModel = courseViewModel,
                courseId = courseId
            )
        }

        // --- DETALLE DE LECCIÓN ---
        composable(
            route = "lesson_detail/{lessonId}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(
                navController = navController,
                lessonId = lessonId
            )
        }

        // --- PERFIL ---
        composable("profile") {
            // ✅ CORREGIDO: Pasamos los callbacks requeridos
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    // Ejecutamos la lógica de logout del ViewModel
                    authViewModel.logout()
                    // Navegamos al login y limpiamos toda la pila
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}