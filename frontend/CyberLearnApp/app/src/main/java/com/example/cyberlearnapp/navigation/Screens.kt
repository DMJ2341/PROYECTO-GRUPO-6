package com.example.cyberlearnapp.navigation

sealed class Screens(val route: String) {
    object Auth : Screens("auth_screen")
    object Dashboard : Screens("dashboard")
    object Courses : Screens("courses")
    object Achievements : Screens("achievements")
    object Profile : Screens("profile")

    object CourseDetail {
        const val route = "course/{courseId}"
        fun createRoute(courseId: Int) = "course/$courseId"
    }

    // No necesitas un objeto para lección si usas el string directo,
    // pero es buena práctica tenerlo si quieres:
    object Lesson {
        const val route = "lesson/{lessonId}"
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
}