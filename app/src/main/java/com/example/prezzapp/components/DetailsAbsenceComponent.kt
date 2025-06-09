package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.prezzapp.Screen
import com.example.prezzapp.model.Absence
import com.example.prezzapp.model.Presence
import com.example.prezzapp.service.AdminService
import kotlinx.coroutines.launch

@Composable
fun DetailsAbscenceComponent(id : Int = 1, modifier: Modifier = Modifier, navController: NavController, activity: ComponentActivity) {

    var absence by remember {
        mutableStateOf<Absence?>(null) // Replace with actual data fetching logic
    }
    LaunchedEffect(Unit) {
        absence = AdminService(activity).getAbsenceById(id)
        if (absence == null) {
            // Handle the case where absence is not found
            navController.navigate(Screen.MainAdminScreen.route)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()

    ){
        Header()
        Column{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal= 30.dp),
                horizontalArrangement = Arrangement.spacedBy(75.dp)

            ){
                Column {
                    Text(
                        text = absence?.user?.name ?: "Nom de l'étudiant",
                    )
                    Text(
                        text = "abscence.date"
                    )

                }
                Spacer(Modifier.weight(1f))
                Box{
                    Text(
                        text = "Matiere",
                        modifier  = Modifier.padding()
                    )
                }

            }
        }
        Column {
            Button(
                onClick = {
                    activity.lifecycleScope.launch {
                        AdminService(activity).downloadJustitf(absence?.presence?.lien ?: "", absence!!)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Télécharger le justificatif d'absence")
            }

            Button(
                onClick = {
                    navController.navigate( route= Screen.DetailsUserAdmin.withArgs(absence?.user?.id ?: 0))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Voir plus de détails sur l'étudiant")
            }
            Button(
                onClick = {
                    activity.lifecycleScope.launch {
                        AdminService(activity).justifyAbsence(absence?.presence!!.id)
                        navController.navigate(Screen.MainAdminScreen.route)
                    }                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Justifier l'absence")
            }
        }
    }

}
