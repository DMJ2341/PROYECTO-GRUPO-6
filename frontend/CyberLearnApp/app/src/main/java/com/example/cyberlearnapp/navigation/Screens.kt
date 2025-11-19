package com.example.cyberlearnapp.navigation

sealed class Screens(val route: String) {
    object Auth : Screens("auth")
    object Main : Screens("main")
    object Dashboard : Screens("main/dashboard")
    object Courses : Screens("main/courses")
    object Achievements : Screens("main/achievements")
    object Profile : Screens("main/profile")
}