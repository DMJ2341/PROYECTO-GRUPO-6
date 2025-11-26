package com.example.cyberlearnapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.ui.screens.final_exam.FinalExamIntroScreen
import com.example.cyberlearnapp.ui.screens.final_exam.FinalExamResultScreen
import com.example.cyberlearnapp.ui.screens.final_exam.FinalExamScreen
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
        // --- AUTENTICACIÓN ---
        composable(route = Screens.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // --- DASHBOARD (HOME) ---
        composable(route = Screens.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // --- LISTA DE CURSOS ---
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

        // --- DETALLE DEL CURSO (Lista de lecciones) ---
        composable(
            route = Screens.CourseDetail.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 1

            CourseDetailScreen(
                courseId = courseId,
                navController = navController // Pasamos el controller para que pueda navegar a la lección
            )
        }

        // --- LECCIÓN (SISTEMA NUEVO) ---
        // ✅ ESTA ES LA RUTA QUE FALTABA Y SOLUCIONA EL PROBLEMA
        composable(
            route = "lesson/{lessonId}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType } // ID es String ("1_1")
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""

            LessonDetailScreen(
                lessonId = lessonId,
                navController = navController
            )
        }

        // --- TEST VOCACIONAL ---
        composable("preference_test") {
            PreferenceTestScreen(navController = navController)
        }

        composable("preference_result") {
            PreferenceResultScreen(
                profileType = "saved",
                navController = navController
            )
        }

        // --- EXAMEN FINAL INTEGRADOR ---
        composable("final_exam/intro") {
            FinalExamIntroScreen(navController)
        }
        composable("final_exam/take") {
            FinalExamScreen(navController)
        }
        composable("final_exam/result") {
            FinalExamResultScreen(navController)
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