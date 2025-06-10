package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.prezzapp.service.AdminService
import kotlinx.coroutines.launch

@Composable
fun AdminAddCours(navController: NavController, activity: ComponentActivity) {
    var nomProf by remember { mutableStateOf("") }
    var spe by remember { mutableStateOf("") }
    var groupe by remember { mutableStateOf("") }
    var heure by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var annee by remember { mutableStateOf("") }
    var matiere by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajouter un cours",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nomProf,
            onValueChange = { nomProf = it },
            label = { Text("Nom du prof") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = spe,
            onValueChange = { spe = it },
            label = { Text("Spécialité") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = groupe,
            onValueChange = { groupe = it },
            label = { Text("Groupe") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = heure,
            onValueChange = { heure = it },
            label = { Text("Heure") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (jj/mm/aa)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = annee,
            onValueChange = { annee = it },
            label = { Text("Année (1A, 2A, 3A)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = matiere,
            onValueChange = { matiere = it },
            label = { Text("Matière") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                activity.lifecycleScope.launch {
                    AdminService(activity).addCours(
                        nomProf = nomProf,
                        spe = spe,
                        groupe = groupe,
                        heure = heure,
                        date = date,
                        annee = annee,
                        matiere = matiere
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Valider",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
