package com.example.prezzapp

sealed class Screen(val route: String) {
    object MainAdminScreen : Screen("main_admin_screen")
    object JustifAdminScreen : Screen("justif_admin_screen")
    object DetailsUserAdmin : Screen("details_user_admin")
    object AddCoursAdmin : Screen("add_cours_admin")
    object AllJustifAdminScreen : Screen("all_justif_admin_screen")
    object SearchStudent : Screen("search_student")
    object Matiere : Screen("matiere")

    fun withArgs(vararg args : Any) : String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

}