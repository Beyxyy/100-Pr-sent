package com.example.prezzapp.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int,
    val login: String,
    val mdp: String,
    val nom: String,
    val role: String,
    val td: String,
    val tp: String,
    val annee: String,
    val spe: String,
    val statut: String
)
