package com.example.cyberlearnapp.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.viewmodel.AuthViewModel
import com.example.cyberlearnapp.viewmodel.CourseViewModel
import com.example.cyberlearnapp.viewmodel.UserViewModel
import androidx.compose.ui.Modifier

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    courseViewModel: CourseViewModel
) {
    navigation(
        startDestination = Screens.Dashboard.route, // ✅ CORREGIDO: Dashboard como inicio
        route = Screens.Main.route // ✅ CORREGIDO: Main es el contenedor
    ) {
        // ✅ TODAS las pantallas dentro del main graph
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
                courseViewModel = courseViewModel,
                onCourseClick = { courseId, courseTitle, courseDescription, courseLevel, courseXp, courseEmoji ->
                    navController.navigate("course_detail/$courseId/$courseTitle/$courseDescription/$courseLevel/$courseXp/$courseEmoji")
                }
            )
        }

        composable(Screens.Achievements.route) {
            AchievementsScreen()
        }

        composable(Screens.Profile.route) {
            ProfileScreen(
                modifier = Modifier
            )
        }

        // ✅ MANTENER las rutas de detalle de cursos y lecciones
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

            CourseDetailScreen(
                courseViewModel = courseViewModel,
                courseId = courseId,
                courseTitle = courseTitle,
                courseDescription = courseDescription,
                courseLevel = courseLevel,
                courseXp = courseXp,
                courseEmoji = courseEmoji,
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = "lesson/{lessonId}/{lessonTitle}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("lessonTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val lessonTitle = backStackEntry.arguments?.getString("lessonTitle") ?: ""

            LessonScreen(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                onNavigateBack = { navController.navigateUp() },
                onLessonCompleted = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = "interactive_lesson/{lessonId}/{lessonTitle}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("lessonTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val lessonTitle = backStackEntry.arguments?.getString("lessonTitle") ?: ""

            SimpleLessonScreen(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                onNavigateBack = { navController.navigateUp() },
                onLessonCompleted = {
                    navController.navigateUp()
                }
            )
        }
    }
}