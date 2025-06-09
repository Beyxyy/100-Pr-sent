package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prezzapp.Screen


@Composable
fun Navigation(modifier: Modifier = Modifier, activity: ComponentActivity) {
    val navController = rememberNavController()
    var menuExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {

    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {

        Header()
        androidx.compose.material3.IconButton(
            onClick = { menuExpanded.value = true },
            modifier = Modifier.padding(8.dp).align(androidx.compose.ui.Alignment.End)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Menu,
                contentDescription = "Menu"
            )
        }
        androidx.compose.material3.DropdownMenu(
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = false }
        ) {
            androidx.compose.material3.DropdownMenuItem(
                text = { androidx.compose.material3.Text("Accueil") },
                onClick = {
                    navController.navigate(Screen.MainAdminScreen.route)
                    menuExpanded.value = false
                }
            )
            androidx.compose.material3.DropdownMenuItem(
                text = { androidx.compose.material3.Text("Se déconnecter") },
                onClick = { /* Ne fait rien */ }
            )
        }
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
            DetailsAbscenceComponent(navController = navController,  id = entry.arguments!!.getInt("id"), activity = activity)
        }

        composable(
            route = Screen.DetailsUserAdmin.route +"/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.IntType
                    nullable = false
                }
            )
        ){entry ->
            UserDetails(navController = navController,  id = entry.arguments!!.getInt("id"), activity  = activity)
        }

        composable(route = Screen.AddCoursAdmin.route)
        {
            AdminAddCours(navController = navController, activity = activity)
        }

        composable(
            route = Screen.AllJustifAdminScreen.route
        ){
            JustifPage(activity = activity, navController = navController)
        }

        composable(
            route = Screen.SearchStudent.route
        ){
            SearchStudent(navController = navController, activity = activity)
        }
        }
    }

}