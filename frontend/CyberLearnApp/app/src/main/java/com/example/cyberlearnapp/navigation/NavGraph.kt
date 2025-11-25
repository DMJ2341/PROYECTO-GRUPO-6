package com.example.cyberlearnapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.ui.screens.preference_test.PreferenceResultScreen
import com.example.cyberlearnapp.ui.screens.preference_test.PreferenceTestScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screens.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- AUTH ---
        composable(route = Screens.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // --- DASHBOARD ---
        composable(route = Screens.Dashboard.route) {
            DashboardScreen(
                navController = navController // Pasamos el navController directo para manejar las rutas nuevas
            )
        }

        // --- PREFERENCE TEST (Paso 1: Rutas Nuevas) ---
        composable("preference_test") {
            PreferenceTestScreen(
                navController = navController
            )
        }

        composable("preference_result") {
            // Asumimos que si navega directo, cargará el resultado guardado
            PreferenceResultScreen(
                profileType = "saved", // "saved" indicará al ViewModel cargar desde backend
                navController = navController
            )
        }

        // --- CURSOS ---
        composable(route = Screens.Courses.route) {
            CoursesScreen(
                onCourseClick = { courseId ->
                    navController.navigate(Screens.CourseDetail.createRoute(courseId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- DETALLE DE CURSO ---
        composable(
            route = Screens.CourseDetail.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 1
            CourseDetailScreen(
                courseId = courseId,
                onLessonClick = { lessonId ->
                    navController.navigate(Screens.InteractiveLesson.createRoute(lessonId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- LECCIÓN INTERACTIVA ---
        composable(
            route = Screens.InteractiveLesson.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            InteractiveLessonScreen(
                lessonId = lessonId,
                onComplete = {
                    navController.popBackStack()
                }
            )
        }

        // --- LOGROS ---
        composable(route = Screens.Achievements.route) {
            AchievementsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- PERFIL ---
        composable(route = Screens.Profile.route) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screens.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}