// src/main/java/com/example/prezzapp/data/models.kt (ou le chemin approprié)
package com.example.prezzapp.data

import java.io.Serializable // Pour passer l'objet entre les activités

data class Absence(
    val id: String, // Identifiant unique de l'absence
    val courseName: String,
    val date: String, // Au format "JJ/MM/AAAA" ou "AAAA-MM-JJ"
    val professorName: String,
    var isJustified: Boolean
) : Serializable // Permet de passer l'objet dans un Intent