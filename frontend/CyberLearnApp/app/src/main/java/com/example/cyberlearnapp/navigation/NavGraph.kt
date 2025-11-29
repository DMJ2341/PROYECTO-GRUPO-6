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

        /* ----------  AUTH  ---------- */
        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToVerification = { email ->
                    navController.navigate("email_verification/$email")
                }
            )
        }

        /* ----------  EMAIL VERIFICATION  ---------- */
        composable(
            route = "email_verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                onVerificationSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        /* ----------  DASHBOARD  ---------- */
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        /* ----------  COURSES  ---------- */
        composable("courses") {
            val courseViewModel: CourseViewModel = hiltViewModel()
            CoursesScreen(navController = navController, viewModel = courseViewModel)
        }

        /* ----------  GLOSARIO  ---------- */
        composable("glossary") {
            val glossaryViewModel: GlossaryViewModel = hiltViewModel()
            GlossaryScreen(navController = navController, viewModel = glossaryViewModel)
        }

        /* ----------  PROFILE  ---------- */
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToBadges = { navController.navigate("badges") }
            )
        }

        /* ----------  BADGES  ---------- */
        composable("badges") {
            BadgesScreen(onBackClick = { navController.popBackStack() })
        }

        /* ----------  COURSE DETAIL  ---------- */
        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            val courseViewModel: CourseViewModel = hiltViewModel()
            CourseDetailScreen(navController = navController, courseId = courseId, viewModel = courseViewModel)
        }

        /* ----------  LESSON DETAIL  ---------- */
        composable(
            route = "lesson_detail/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(navController = navController, lessonId = lessonId)
        }
    }
}