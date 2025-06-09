package com.example.prezzapp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.material.search.SearchBar


@Composable
fun SearchStudent(activity: ComponentActivity, navController: NavController) {
    var searchQuery by remember { mutableStateOf<String>("") }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }

    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { androidx.compose.material3.Text("Rechercher un étudiant") },
        modifier = Modifier.fillMaxWidth()
    )

}