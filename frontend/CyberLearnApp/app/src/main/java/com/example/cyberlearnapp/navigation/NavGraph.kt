package com.example.cyberlearnapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.cyberlearnapp.ui.screens.*
import com.example.cyberlearnapp.utils.AuthManager
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

        /* ----------  GLOSARIO FLOW (Shared ViewModel) ---------- */
        navigation(startDestination = "glossary_main", route = "glossary") {

            composable("glossary_main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("glossary")
                }
                val glossaryViewModel: GlossaryViewModel = hiltViewModel(parentEntry)
                GlossaryScreen(navController = navController, viewModel = glossaryViewModel)
            }

            composable("flashcard") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("glossary")
                }
                val glossaryViewModel: GlossaryViewModel = hiltViewModel(parentEntry)
                FlashcardScreen(navController = navController, viewModel = glossaryViewModel)
            }

            composable("quiz") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("glossary")
                }
                val glossaryViewModel: GlossaryViewModel = hiltViewModel(parentEntry)
                QuizScreen(navController = navController, viewModel = glossaryViewModel)
            }

            composable(
                route = "practice_result/{correct}/{total}",
                arguments = listOf(
                    navArgument("correct") { type = NavType.IntType },
                    navArgument("total") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val correct = backStackEntry.arguments?.getInt("correct") ?: 0
                val total = backStackEntry.arguments?.getInt("total") ?: 0

                PracticeResultScreen(
                    navController = navController,
                    correctCount = correct,
                    totalCount = total
                )
            }
        }

        /* ----------  🎯 TEST DE PREFERENCIAS FLOW (5 Pantallas + Intro) ---------- */
        // Todo el flujo ahora está dentro de un único grafo anidado
        navigation(
            startDestination = "preference_test_intro",   // <-- Intro es el inicio
            route = Screens.PreferenceTest.route          // <-- Ruta raíz del grafo
        ) {
            // 0. Pantalla de Introducción
            composable("preference_test_intro") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.PreferenceTest.route)
                }
                val testViewModel: TestViewModel = hiltViewModel(parentEntry)

                PreferenceTestIntroScreen(
                    navController = navController,
                    viewModel = testViewModel
                )
            }

            // 1. Pantalla de Preguntas
            composable("test_questions") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.PreferenceTest.route)
                }
                val testViewModel: TestViewModel = hiltViewModel(parentEntry)
                val token = AuthManager.getToken() ?: ""

                TestScreen(
                    navController = navController,
                    viewModel = testViewModel,
                    token = token
                )
            }

            // 2. Resumen del Resultado
            composable("test_result_summary") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.PreferenceTest.route)
                }
                val testViewModel: TestViewModel = hiltViewModel(parentEntry)

                TestResultSummaryScreen(
                    navController = navController,
                    viewModel = testViewModel
                )
            }

            // 3. Recomendaciones
            composable("test_recommendations") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.PreferenceTest.route)
                }
                val testViewModel: TestViewModel = hiltViewModel(parentEntry)

                TestRecommendationsScreen(
                    navController = navController,
                    viewModel = testViewModel
                )
            }

            // 4. Skills Detalladas
            composable("test_skills") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.PreferenceTest.route)
                }
                val testViewModel: TestViewModel = hiltViewModel(parentEntry)

                TestSkillsScreen(
                    navController = navController,
                    viewModel = testViewModel
                )
            }
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
            CourseDetailScreen(
                navController = navController,
                courseId = courseId,
                viewModel = courseViewModel
            )
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