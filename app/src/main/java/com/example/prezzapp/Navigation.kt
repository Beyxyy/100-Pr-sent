package com.example.prezzapp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prezzapp.components.DetailsAbscenceComponent
import com.example.prezzapp.components.HomeContainer


@Composable
fun Navigation(modifier: Modifier = Modifier, activity: ComponentActivity) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainAdminScreen.route)
    {
        composable(route= Screen.MainAdminScreen.route) {
            HomeContainer(navController = navController, activity = activity)
        }
        composable(
            route = Screen.JustifAdminScreen.route +"/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.IntType
                    nullable = false
                }
            )
        ){entry ->
          DetailsAbscenceComponent(navController = navController,  id = entry.arguments!!.getInt("id") )
        }
    }
}