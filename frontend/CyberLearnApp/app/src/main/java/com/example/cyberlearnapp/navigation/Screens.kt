package com.example.cyberlearnapp.navigation

sealed class Screens(val route: String) {
    object Auth : Screens("auth_screen")
    object Dashboard : Screens("dashboard")
    object Courses : Screens("courses")
    object Glossary : Screens("glossary")
    object Profile : Screens("profile")
    object PreferenceTest : Screens("preference_test")
    object PreferenceResult : Screens("preference_result")

    object CourseDetail {
        const val route = "course_detail/{courseId}"
        fun createRoute(courseId: Int) = "course_detail/$courseId"
    }

    object Lesson {
        const val route = "lesson/{lessonId}"
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
}