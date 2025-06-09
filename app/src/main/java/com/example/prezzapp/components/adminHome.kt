package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.Screen
import com.example.prezzapp.model.Absence
import com.example.prezzapp.service.AdminService

@Composable
fun HomeContainer(modifier: Modifier = Modifier, navController: NavController, activity: ComponentActivity) {

    var search by remember {
        mutableStateOf("Chercher un étudiant")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()

    ) {
        Text(
            text = "Bonjour admin, vous pouvez  : ",
        )
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.AddCoursAdmin.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Ajouter un cours",
                )
            }

            Button(
                onClick = {
                    navController.navigate(Screen.AllJustifAdminScreen.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Voir les justificatifs",
                )
            }


            Button(
                onClick = {
                    navController.navigate(Screen.SearchStudent.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Chercher un étudiant",
                )
            }

        }


    }
}