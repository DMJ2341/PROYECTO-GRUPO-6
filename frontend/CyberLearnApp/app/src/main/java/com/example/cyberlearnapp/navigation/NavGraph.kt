package com.example.cyberlearnapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cyberlearnapp.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screens.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Autenticación
        composable(route = Screens.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard
        composable(route = Screens.Dashboard.route) {
            DashboardScreen(
                onNavigateToCourses = {
                    navController.navigate(Screens.Courses.route)
                },
                onNavigateToCourse = { courseId ->
                    navController.navigate(Screens.CourseDetail.createRoute(courseId))
                },
                onNavigateToAchievements = {
                    navController.navigate(Screens.Achievements.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screens.Profile.route)
                },
                onLogout = {
                    navController.navigate(Screens.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Cursos
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

        // Detalle de curso
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

        // Lección interactiva
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

        // Logros
        composable(route = Screens.Achievements.route) {
            AchievementsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Perfil
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