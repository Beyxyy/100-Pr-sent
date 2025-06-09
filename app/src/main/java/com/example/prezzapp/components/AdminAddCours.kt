package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.prezzapp.service.AdminService
import kotlinx.coroutines.launch

@Composable
fun AdminAddCours(navController: NavController, activity: ComponentActivity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ) {

        Text(text = "Ajouter un cours")
            var nomProf by remember { mutableStateOf("") }
            var spe by remember { mutableStateOf("") }
            var groupe by remember { mutableStateOf("") }
            var heure by remember { mutableStateOf("") }
            var date by remember { mutableStateOf("") }
            var annee by remember { mutableStateOf("") }
            var matiere by remember { mutableStateOf("") }


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
                label = { Text("date sous forme jj/mm/aa") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = annee,
                onValueChange = { annee = it },
                label = { Text("annee : 1A ou 2A ou 3A") },
                modifier = Modifier.fillMaxWidth()
            )
        OutlinedTextField(
            value = matiere,
            onValueChange = { matiere = it },
            label = { Text("matiere") },
            modifier = Modifier.fillMaxWidth()
        )



            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
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
            }
            ) {
                Text("Valider", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

    }
}