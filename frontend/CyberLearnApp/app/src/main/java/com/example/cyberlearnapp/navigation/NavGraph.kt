package com.example.cyberlearnapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "auth"
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- AUTH ---
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
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

        // --- COURSES ---
        composable("courses") {
            val courseViewModel: CourseViewModel = hiltViewModel()
            CoursesScreen(navController = navController, viewModel = courseViewModel)
        }

        // --- ✅ GLOSARIO ---
        composable("glossary") {
            val glossaryViewModel: GlossaryViewModel = hiltViewModel()
            GlossaryScreen(navController = navController, viewModel = glossaryViewModel)
        }

        // --- PROFILE ---
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("auth") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // --- DETALLE CURSO ---
        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            val courseViewModel: CourseViewModel = hiltViewModel()
            CourseDetailScreen(
                navController = navController,
                viewModel = courseViewModel,
                courseId = courseId
            )
        }

        // --- DETALLE LECCIÓN ---
        composable(
            route = "lesson_detail/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            // LessonViewModel se inyecta dentro de la pantalla
            LessonDetailScreen(
                navController = navController,
                lessonId = lessonId
            )
        }
    }
}