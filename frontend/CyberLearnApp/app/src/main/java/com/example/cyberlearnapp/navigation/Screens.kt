package com.example.cyberlearnapp.navigation

sealed class Screens(val route: String) {
    object Auth : Screens("auth_screen")
    object Dashboard : Screens("dashboard")
    object Courses : Screens("courses")
    // object Achievements : Screens("achievements") // Eliminado
    object Glossary : Screens("glossary") // ✅ Añadido
    object Profile : Screens("profile")

    object CourseDetail {
        const val route = "course/{courseId}"
        fun createRoute(courseId: Int) = "course/$courseId"
    }

    object Lesson {
        const val route = "lesson/{lessonId}"
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
}