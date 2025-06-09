package com.example.prezzapp.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.Screen
import com.example.prezzapp.model.Absence

@Composable
fun absenceComponent(absence : Absence, navController: NavController){
    Button(
        onClick =  {
            navController.navigate(
                route = Screen.JustifAdminScreen.withArgs(absence.presence.id),
            )
        }
        ,
        modifier = Modifier
            .padding(5.dp)
            .background(Color.White)
    ) {

        Row {
            Text(
                text = absence.user.name,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = absence.cours.jour + " " + absence.cours.heure,
            )
        }

    }
}