package com.example.prezzapp.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Header() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(75.dp)
            .fillMaxWidth()
    ){
        Text(
            text = "Prezzap"
        )
    }
}