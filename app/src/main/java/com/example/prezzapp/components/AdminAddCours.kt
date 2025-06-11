package com.example.prezzapp.components

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.prezzapp.service.AdminService
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddCours(navController: NavController, activity: ComponentActivity) {
    var nomProf by remember { mutableStateOf("") }
    var spe by remember { mutableStateOf("") }
    var groupe by remember { mutableStateOf("") }
    var heure by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var annee by remember { mutableStateOf("") }
    var matiere by remember { mutableStateOf("") }

    var timeExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val context = LocalContext.current

    // Fixed time ranges
    val timeRanges = listOf(
        "08h-10h",
        "10h-12h",
        "14h-16h",
        "16h-18h"
    )

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

        var profExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = profExpanded,
            onExpandedChange = { profExpanded = !profExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = nomProf ?: "Sélectionner un prof",
                onValueChange = {},
                label = { Text("Prof") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = profExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = profExpanded,
                onDismissRequest = { profExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Joel Dion") },
                    onClick = {
                        nomProf = "Joel Dion"
                        profExpanded = false
                    }

                )
            }
        }

        var speExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = speExpanded,
            onExpandedChange = { speExpanded = !speExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = spe ?: "Sélectionner une spécialité",
                onValueChange = {},
                label = { Text("Spécialité") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = speExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = speExpanded,
                onDismissRequest = { speExpanded = false }
            ) {
                    DropdownMenuItem(
                        text = { Text("IR") },
                        onClick = {
                            spe = "IR"
                            speExpanded = false
                        }

                    )
            }
        }

        var groupeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = groupeExpanded,
            onExpandedChange = { groupeExpanded = !groupeExpanded },
            modifier = Modifier.fillMaxWidth()

        ) {
            OutlinedTextField(
                readOnly = true,
                value = groupe ?: "Sélectionner un groupe",
                onValueChange = {},
                label = { Text("Groupe") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupeExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = groupeExpanded,
                onDismissRequest = { groupeExpanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text("TP1") },
                    onClick = {
                        groupe = "TP1"
                        groupeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("TP2") },
                    onClick = {
                        groupe = "TP2"
                        groupeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("TP3") },
                    onClick = {
                        groupe = "TP3"
                        groupeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("TD1") },
                    onClick = {
                        groupe = "TD1"
                        groupeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("TD2") },
                    onClick = {
                        groupe = "TD2"
                        groupeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("CM") },
                    onClick = {
                        groupe = "CM"
                        groupeExpanded = false
                    }
                )
            }

        }



        // Time selector
        ExposedDropdownMenuBox(
            expanded = timeExpanded,
            onExpandedChange = { timeExpanded = !timeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = heure ?: "Sélectionner une heure",
                onValueChange = {},
                label = { Text("Heure") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = timeExpanded,
                onDismissRequest = { timeExpanded = false }
            ) {
                timeRanges.forEach { h ->
                    DropdownMenuItem(
                        text = { Text(text = h) },
                        onClick = {
                            heure = h
                            timeExpanded = false
                        }
                    )
                }
            }
        }

        // Date picker
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = date?.format(dateFormatter) ?: "Sélectionner une date",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            date = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }) {
                        Text("Sélectionner")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        var anneeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = anneeExpanded,
            onExpandedChange = { anneeExpanded = !anneeExpanded },
            modifier = Modifier.fillMaxWidth()

        ) {
            OutlinedTextField(
                readOnly = true,
                value = annee ?: "Sélectionner une année",
                onValueChange = {},
                label = { Text("Année") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = anneeExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = anneeExpanded,
                onDismissRequest = { anneeExpanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text("1A") },
                    onClick = {
                        annee = "1A"
                        anneeExpanded = false
                    }
                )

            }
        }


        var matiereExpanded by remember { mutableStateOf(false) }
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



        Button(
            onClick = {
                if (heure == null || date == null) {
                    Toast.makeText(context, "Veuillez sélectionner une heure et une date", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                activity.lifecycleScope.launch {
                    AdminService(activity).addCours(
                        nomProf = nomProf,
                        spe = spe,
                        groupe = groupe,
                        heure = heure!!.format(timeFormatter),
                        date = date!!.format(dateFormatter),
                        annee = annee,
                        matiere = matiere,
                        context
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Valider")
        }
    }
}
