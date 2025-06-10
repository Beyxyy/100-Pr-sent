package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prezzapp.Screen

@Composable
fun HomeContainer(modifier: Modifier = Modifier, navController: NavController, activity: ComponentActivity) {

    var search by remember {
        mutableStateOf("Chercher un étudiant")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Bienvenue, Admin 👋",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Que souhaitez-vous faire ?",
            fontSize = 18.sp,
            color = Color.Gray
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdminActionButton("➕ Ajouter un cours") {
                navController.navigate(Screen.AddCoursAdmin.route)
            }
            AdminActionButton("📄 Voir les justificatifs") {
                navController.navigate(Screen.AllJustifAdminScreen.route)
            }
            AdminActionButton("🔍 Chercher un étudiant") {
                navController.navigate(Screen.SearchStudent.route)
            }
        }
    }
}

@Composable
fun AdminActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
