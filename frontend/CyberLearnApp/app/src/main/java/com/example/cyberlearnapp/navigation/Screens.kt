package com.example.cyberlearnapp.navigation

sealed class Screens(val route: String) {
    object Auth : Screens("auth")
    object Main : Screens("main")
    object Dashboard : Screens("dashboard")
    object Courses : Screens("courses")
    object Achievements : Screens("achievements")
    object Profile : Screens("profile")
}