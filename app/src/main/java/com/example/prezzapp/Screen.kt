package com.example.prezzapp

sealed class Screen(val route: String) {
    object MainAdminScreen : Screen("main_admin_screen")
    object JustifAdminScreen : Screen("justif_admin_screen")


    fun withArgs(vararg args : Int) : String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

}