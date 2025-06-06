package com.example.prezzapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cours(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val prof: String,
    val jour: String,
    val heure: String,
    val groupe : String,
    val annee: String,
    val spe: String

)
