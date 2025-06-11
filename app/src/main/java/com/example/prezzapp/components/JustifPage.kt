package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prezzapp.model.Absence
import com.example.prezzapp.service.AdminService
import com.example.prezzapp.Screen



@Composable
fun JustifPage(activity: ComponentActivity, navController: NavController) {
    var absenceNotJustified by remember { mutableStateOf<List<Absence>>(emptyList()) }

    LaunchedEffect(Unit) {
        absenceNotJustified = AdminService(activity).getAbsencesNotJustified()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF9FAFB))
    ) {
        Text(
            text = "Justificatifs à vérifier",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (absenceNotJustified.isEmpty()) {
            Text(
                text = "Aucun justificatif à vérifier",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
            )
        } else {
            for (absence in absenceNotJustified) {
                AbsenceCard(absence = absence) {
                    navController.navigate(Screen.JustifAdminScreen.withArgs(absence.presence.id))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AbsenceCard(absence: Absence, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Étudiant: ${absence.user.name ?: "N/A"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cours: ${absence.cours.matiere} - ${absence.cours.jour} à ${absence.cours.heure}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF555555)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Justifié: ${if (absence.presence.estJustifie) "Oui" else "Non"}",
                style = MaterialTheme.typography.bodySmall,
                color = if (absence.presence.estJustifie) Color(0xFF4CAF50) else Color(0xFFF44336),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
