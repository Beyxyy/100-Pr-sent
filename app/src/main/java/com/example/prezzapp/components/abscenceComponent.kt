package com.example.prezzapp.components

import android.graphics.drawable.shapes.Shape
import android.widget.GridLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.prezzapp.Screen

@Composable
fun absenceComponent(id:Int, navController: NavController){
    Button(
        onClick =  {
            navController.navigate(
                route = Screen.JustifAdminScreen.withArgs(id),
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                )
                .background(
                    color = Color(0,0,0,1)
                )
        ) {
            Column {
                Text(
                    text = "absence.date"
                )
                Text(
                    text = "absence.User.name"
                )
            }
        }
    }
}