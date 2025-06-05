package com.example.prezzapp.data

import java.io.Serializable

data class Absence(
    val id: String,
    val courseName: String,
    val date: String,
    val professorName: String,
    var isJustified: Boolean
) : Serializable