package com.example.prezzapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.BaseActivity
import com.example.prezzapp.model.Absence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.service.AdminService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebriefMatiere(navController: NavController, activity: BaseActivity) {
    var absences by remember { mutableStateOf(listOf<Absence>()) }
    var matiere by remember { mutableStateOf("") }
    LaunchedEffect(matiere) {
        absences = AdminService(activity).getAbsenceByMatiere(matiere)
    }



    var matiereExpanded by remember { mutableStateOf(false) }

    Column{
        ExposedDropdownMenuBox(
            expanded = matiereExpanded,
            onExpandedChange = { matiereExpanded = !matiereExpanded },
            modifier = Modifier.fillMaxWidth()

        ) {
            OutlinedTextField(
                readOnly = true,
                value = matiere ?: "Sélectionner une matiere",
                onValueChange = {},
                label = { Text("Matiere") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = matiereExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = matiereExpanded,
                onDismissRequest = { matiereExpanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text("Calcul Matriciel") },
                    onClick = {
                        matiere = "Calcul Matriciel"
                        matiereExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Stats") },
                    onClick = {
                        matiere = "Stats"
                        matiereExpanded = false
                    }
                )
            }

        }
    Text(
        text = "Absence pour la matière $matiere : ${absences.size} absence(s)",
        modifier = Modifier
            .padding(16.dp)
    )
    if (absences.isEmpty()) {
        // Display a message if there are no absences for this subject
        Text(
            text = "Aucune absence pour la matière $matiere",
            modifier = Modifier
                .padding(16.dp)
        )
    } else {
        // Display the list of absences for the subject
        Column {
            Text(
                text = "Liste des absences pour la matière $matiere :",
                modifier = Modifier
                    .padding(16.dp)
            )
            for (absence in absences) {
                if( absence.estJustifie == false) {
                    absenceComponent(absence = absence, navController = navController)

                }
                }
            }
        }
    }
}