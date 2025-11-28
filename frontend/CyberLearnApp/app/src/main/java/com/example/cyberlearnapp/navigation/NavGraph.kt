package com.example.cyberlearnapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    startDestination: String = "auth",
    paddingValues: PaddingValues
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues)
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

        // --- GLOSARIO ---
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
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                // ✅ NUEVO: Navegación a pantalla de badges
                onNavigateToBadges = {
                    navController.navigate("badges")
                }
            )
        }

        // --- ✅ NUEVO: PANTALLA DE BADGES ---
        composable("badges") {
            BadgesScreen(
                onBackClick = { navController.popBackStack() }
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
                courseId = courseId,
                viewModel = courseViewModel
            )
        }

        // --- DETALLE LECCIÓN ---
        composable(
            route = "lesson_detail/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(
                navController = navController,
                lessonId = lessonId
            )
        }
    }
}