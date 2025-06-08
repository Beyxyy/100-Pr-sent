package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.model.Absence
import com.example.prezzapp.service.AdminService

@Composable
fun HomeContainer(modifier: Modifier = Modifier, navController: NavController, activity: ComponentActivity) {

    var search by remember {
        mutableStateOf("Chercher un étudiant")
    }
    var absenceNotJustified by remember { mutableStateOf<List<Absence>>(emptyList()) }
    LaunchedEffect(Unit) {
        absenceNotJustified = AdminService(activity).getAbsencesNotJustified()
    }
    Column {
    Header()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(horizontal= 30.dp)
    ){
        Text(
            text = "bonjour admin",
        )
        Spacer(Modifier.height(20.dp))

        Text(
            text = "Justificatifs à vérifier"
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
        if (absenceNotJustified.isEmpty()) {
            Text(
                text = "Aucun justificatif à vérifier",
            )
        }
            for (absence: Absence in absenceNotJustified){
                absenceComponent(absence, navController)
            }
        }

    }
    }


}