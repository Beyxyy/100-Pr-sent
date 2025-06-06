package com.example.prezzapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Status {
    STUDENT,
    TEACHER
}

@Entity
open class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val login: String,
    val name: String,
    val password: String,
    val td: String,
    val tp: String,
    val annee: String,
    val spe: String,
    val status: Status,
)
