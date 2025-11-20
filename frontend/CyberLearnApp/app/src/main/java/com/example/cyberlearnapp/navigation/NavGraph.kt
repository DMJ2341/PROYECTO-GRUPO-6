package com.example.cyberlearnapp.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.CourseViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    courseViewModel: CourseViewModel
) {
    navigation(
        startDestination = Screens.Dashboard.route,
        route = Screens.Main.route
    ) {
        // ========== DASHBOARD ==========
        composable(Screens.Dashboard.route) {
            DashboardScreen(
                navController = navController
            )
        }

        // ========== COURSES ==========
        composable(Screens.Courses.route) {
            CoursesScreen(
                navController = navController
            )
        }

        // ========== ACHIEVEMENTS ==========
        composable(Screens.Achievements.route) {
            AchievementsScreen()
        }

        // ========== PROFILE ==========
        composable(Screens.Profile.route) {
            ProfileScreen(
                modifier = Modifier
            )
        }

        // ========== COURSE DETAIL ==========
        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0

            println("🎯 [NAV] Navegando a CourseDetail con ID: $courseId")

            CourseDetailScreen(
                courseId = courseId,
                navController = navController
            )
        }

        // ========== LESSON (TEXT) ==========
        composable(
            route = "lesson/{lessonId}/{lessonTitle}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("lessonTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val lessonTitle = backStackEntry.arguments?.getString("lessonTitle") ?: ""

            println("🎯 [NAV] Navegando a Lesson: $lessonId - $lessonTitle")

            LessonScreen(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                onNavigateBack = { navController.navigateUp() },
                onLessonCompleted = {
                    println("✅ [NAV] Lección completada: $lessonId")
                    navController.navigateUp()
                }
            )
        }

        // ========== INTERACTIVE LESSON ==========
        composable(
            route = "interactive_lesson/{lessonId}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""

            println("🎯 [NAV] Navegando a Interactive Lesson: $lessonId")

            InteractiveLessonScreen(
                lessonId = lessonId,
                onNavigateBack = { navController.navigateUp() },
                onLessonCompleted = {
                    println("✅ [NAV] Lección interactiva completada: $lessonId")
                    navController.navigateUp()
                }
            )
        }
    }
}