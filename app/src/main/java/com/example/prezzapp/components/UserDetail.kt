package com.example.prezzapp.components

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.Screen
import com.example.prezzapp.model.Absence
import com.example.prezzapp.model.User
import com.example.prezzapp.service.AdminService

@Composable
fun UserDetails(id : Int, navController: NavController, activity: ComponentActivity) {

    var user by remember{
        mutableStateOf<User?>(null)
    }
    LaunchedEffect(Unit) {
        user = AdminService(activity).getUserById(id)
    }


    var absences by remember {
        mutableStateOf<List<Absence>>(emptyList())
    }
    LaunchedEffect(Unit) {
        absences = AdminService(activity).getAbsenceByUserId(id)
    }



    Column{
        Header()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
        ) {


            Row{
                Text(
                    text = user?.name ?: "User not found",
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = user?.annee ?: "User not found",
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = user?.spe ?: "User not found",
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = user?.td ?: "User not found",
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = user?.tp ?: "User not found",
                )
            }
            for( absence in absences) {
                absenceComponent(absence, navController)
            }

        }
    }


}