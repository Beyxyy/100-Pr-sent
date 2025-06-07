package com.example.prezzapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prezzapp.model.Presence

@Composable
fun DetailsAbscenceComponent(id : Int = 1, modifier: Modifier = Modifier, navController: NavController) {

    val abscense : Presence? = null // chercher l'abscence ID
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()

    ){
        Header()
        Column{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal= 30.dp),
                horizontalArrangement = Arrangement.spacedBy(75.dp)

            ){
                Column {
                    Text(
                        text = "abscence.Student"
                    )
                    Text(
                        text = "abscence.date"
                    )

                }
                Spacer(Modifier.weight(1f))
                Box{
                    Text(
                        text = "Matiere",
                        modifier  = Modifier.padding()
                    )
                }

            }
        }
        Row {

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Télécharger le justificatif d'absence")
            }
        }
    }

}
