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
    var heure by remember { mutableStateOf<LocalTime?>(null) }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var annee by remember { mutableStateOf("") }
    var matiere by remember { mutableStateOf("") }

    var timeExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
    val context = LocalContext.current

    // Fixed time ranges
    val timeRanges = listOf(
        Pair(LocalTime.of(8, 0), LocalTime.of(10, 0)),
        Pair(LocalTime.of(10, 0), LocalTime.of(12, 0)),
        Pair(LocalTime.of(14, 0), LocalTime.of(16, 0)),
        Pair(LocalTime.of(16, 0), LocalTime.of(18, 0))
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

        // Time selector
        ExposedDropdownMenuBox(
            expanded = timeExpanded,
            onExpandedChange = { timeExpanded = !timeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = timeRanges.find { it.first == heure }?.let {
                    "${it.first.format(timeFormatter)} - ${it.second.format(timeFormatter)}"
                } ?: "Sélectionner une heure",
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
                timeRanges.forEach { (start, end) ->
                    DropdownMenuItem(
                        text = { Text("${start.format(timeFormatter)} - ${end.format(timeFormatter)}") },
                        onClick = {
                            heure = start
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
                        matiere = matiere
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
