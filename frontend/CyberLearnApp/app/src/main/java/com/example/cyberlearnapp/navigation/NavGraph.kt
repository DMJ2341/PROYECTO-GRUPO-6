package com.example.cyberlearnapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    navigation(
        startDestination = Screens.Dashboard.route,
        route = Screens.Main.route
    ) {
        composable(Screens.Dashboard.route) {
            DashboardScreen(
                userViewModel = userViewModel,
                onCourseClick = { courseName ->
                    println("🎯 Curso seleccionado: $courseName")
                }
            )
        }

        composable(Screens.Courses.route) {
            CoursesScreen(
                onCourseClick = { courseId, courseTitle, courseDescription, courseLevel, courseXp, courseEmoji ->
                    navController.navigate(
                        "course_detail/$courseId/$courseTitle/$courseDescription/$courseLevel/$courseXp/$courseEmoji"
                    )
                }
            )
        }

        composable(
            route = "course_detail/{courseId}/{courseTitle}/{courseDescription}/{courseLevel}/{courseXp}/{courseEmoji}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("courseTitle") { type = NavType.StringType },
                navArgument("courseDescription") { type = NavType.StringType },
                navArgument("courseLevel") { type = NavType.StringType },
                navArgument("courseXp") { type = NavType.IntType },
                navArgument("courseEmoji") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseTitle = backStackEntry.arguments?.getString("courseTitle") ?: ""
            val courseDescription = backStackEntry.arguments?.getString("courseDescription") ?: ""
            val courseLevel = backStackEntry.arguments?.getString("courseLevel") ?: ""
            val courseXp = backStackEntry.arguments?.getInt("courseXp") ?: 0
            val courseEmoji = backStackEntry.arguments?.getString("courseEmoji") ?: "📚"

            val currentUser by authViewModel.currentUser.collectAsState()
            val token = currentUser?.token ?: ""

            CourseDetailScreen(
                courseId = courseId,
                courseTitle = courseTitle,
                courseDescription = courseDescription,
                courseLevel = courseLevel,
                courseXp = courseXp,
                courseEmoji = courseEmoji,
                token = token,
                navController = navController,  // ← AGREGADO
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Lección normal (texto)
        composable(
            route = "lesson/{lessonId}/{lessonTitle}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("lessonTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val lessonTitle = backStackEntry.arguments?.getString("lessonTitle") ?: ""

            val currentUser by authViewModel.currentUser.collectAsState()
            val token = currentUser?.token ?: ""

            LessonScreen(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                token = token,
                onNavigateBack = { navController.navigateUp() },
                onLessonCompleted = {
                    navController.navigateUp()
                }
            )
        }

        // ✨ NUEVA: Lección interactiva
        composable(
            route = "interactive_lesson/{lessonId}/{lessonTitle}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("lessonTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val lessonTitle = backStackEntry.arguments?.getString("lessonTitle") ?: ""

            val currentUser by authViewModel.currentUser.collectAsState()
            val token = currentUser?.token ?: ""

            InteractiveLessonScreen(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                token = token,
                onNavigateBack = { navController.navigateUp() },
                onLessonCompleted = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screens.Achievements.route) {
            AchievementsScreen()
        }

        composable(Screens.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                onEditProfile = {
                    println("✏️ Editar perfil")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screens.Auth.route) {
                        popUpTo(Screens.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}