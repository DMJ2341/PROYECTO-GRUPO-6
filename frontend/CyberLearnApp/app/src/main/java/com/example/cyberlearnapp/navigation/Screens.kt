package com.example.cyberlearnapp.navigation

/**
 * Rutas de navegación
 * ACTUALIZADO: Agregada ruta para lecciones interactivas
 */
sealed class Screens(val route: String) {
    object Auth : Screens("auth")
    object Dashboard : Screens("dashboard")
    object Courses : Screens("courses")
    object Achievements : Screens("achievements")
    object Profile : Screens("profile")

    object CourseDetail : Screens("course/{courseId}") {
        fun createRoute(courseId: Int) = "course/$courseId"
    }

    // 🆕 NUEVO: Lección interactiva
    object InteractiveLesson : Screens("lesson/{lessonId}") {
        fun createRoute(lessonId: Int) = "lesson/$lessonId"
    }
}