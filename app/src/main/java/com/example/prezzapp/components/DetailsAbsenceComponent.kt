package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.prezzapp.Screen
import com.example.prezzapp.model.Absence
import com.example.prezzapp.service.AdminService
import kotlinx.coroutines.launch

@Composable
fun DetailsAbscenceComponent(
    id: Int = 1,
    modifier: Modifier = Modifier,
    navController: NavController,
    activity: ComponentActivity
) {
    var absence by remember { mutableStateOf<Absence?>(null) }
    LaunchedEffect(Unit) {
        absence = AdminService(activity).getAbsenceById(id)
        if (absence == null) {
            navController.navigate(Screen.MainAdminScreen.route)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Étudiant : ${absence?.user?.name ?: "Nom de l'étudiant"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Absent le : ${absence?.cours?.jour ?: "Date"} à ${absence?.cours?.heure ?: "Heure"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Matière : ${absence?.cours?.matiere ?: "Matière"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate(
                    Screen.DetailsUserAdmin.withArgs(absence?.user?.id ?: 0)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Voir plus de détails sur l'étudiant",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    activity.lifecycleScope.launch {
                        AdminService(activity).downloadJustitf(
                            absence?.presence?.lien ?: "",
                            absence!!
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Télécharger le justificatif d'absence")
            }

            Button(
                onClick = {
                    activity.lifecycleScope.launch {
                        absence?.presence?.id?.let { AdminService(activity).justifyAbsence(it) }
                        navController.navigate(Screen.MainAdminScreen.route)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF388E3C),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Justifier l'absence")
            }
        }
    }
}
