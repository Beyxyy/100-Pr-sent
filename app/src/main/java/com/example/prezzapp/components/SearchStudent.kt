package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prezzapp.R
import com.example.prezzapp.Screen
import com.example.prezzapp.model.User
import com.example.prezzapp.service.AdminService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.Spacer



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchStudent(activity: ComponentActivity, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Debounced search
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isLoading = true
            try {
                val results = withContext(Dispatchers.IO) {
                    AdminService(activity).searchStudentsByName(searchQuery)
                }
                searchResults = results
            } catch (e: Exception) {
                searchResults = emptyList()
            }
            isLoading = false
        } else {
            searchResults = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Rechercher un étudiant",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search field with modern styling
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Nom de l'étudiant") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },

            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
        )

        Spacer(modifier = Modifier.padding(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Chargement...")
                }
            }
            searchResults.isEmpty() && searchQuery.length >= 2 -> {
                Text(
                    text = "Aucun étudiant trouvé",
                    modifier = Modifier.padding(16.dp)
                )
            }
            searchResults.isNotEmpty() -> {
                // Suggestions list with modern card design
                LazyColumn {
                    items(searchResults) { student ->
                        StudentSuggestionItem(
                            student = student,
                            onStudentSelected = {
                                navController.navigate(
                                    Screen.DetailsUserAdmin.withArgs(student.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentSuggestionItem(student: User, onStudentSelected: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onStudentSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = student.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "${student.annee} | ${student.spe}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "TD: ${student.td} | TP: ${student.tp}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}