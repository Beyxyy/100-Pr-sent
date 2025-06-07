package com.example.prezzapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.model.Presence
import com.example.prezzapp.components.Header

@Composable
fun HomeContainer(modifier: Modifier = Modifier, navController: NavController) {

    var search by remember {
        mutableStateOf("Chercher un étudiant")
    }

    val absenceNotJustified : List<Presence> = emptyList()
    Header()
    Column (
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(horizontal= 30.dp)
    ){
       Text(
            text = "Justificatifs à vérifier"
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            for (i: Int in intArrayOf(1,2,3,4)){
                absenceComponent(i, navController)
            }
        }

    }


}