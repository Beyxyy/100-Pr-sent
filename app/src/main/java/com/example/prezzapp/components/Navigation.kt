package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prezzapp.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(modifier: Modifier = Modifier, activity: ComponentActivity) {
    val navController = rememberNavController()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "PrezzApp") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.width(180.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Accueil") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                            onClick = {
                                navController.navigate(Screen.MainAdminScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Se déconnecter") },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                            onClick = {
                                // TODO: Handle logout logic here
                                menuExpanded = false
                            }
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            NavHost(navController = navController, startDestination = Screen.MainAdminScreen.route) {
                composable(route = Screen.MainAdminScreen.route) {
                    HomeContainer(navController = navController, activity = activity)
                }

                composable(
                    route = Screen.JustifAdminScreen.route + "/{id}",
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.IntType
                            nullable = false
                        }
                    )
                ) { entry ->
                    DetailsAbscenceComponent(
                        navController = navController,
                        id = entry.arguments!!.getInt("id"),
                        activity = activity
                    )
                }

                composable(
                    route = Screen.DetailsUserAdmin.route + "/{id}",
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.IntType
                            nullable = false
                        }
                    )
                ) { entry ->
                    UserDetails(
                        navController = navController,
                        id = entry.arguments!!.getInt("id"),
                        activity = activity
                    )
                }

                composable(route = Screen.AddCoursAdmin.route) {
                    AdminAddCours(navController = navController, activity = activity)
                }

                composable(route = Screen.AllJustifAdminScreen.route) {
                    JustifPage(activity = activity, navController = navController)
                }

                composable(route = Screen.SearchStudent.route) {
                    SearchStudent(navController = navController, activity = activity)
                }
            }
        }
    }
}
