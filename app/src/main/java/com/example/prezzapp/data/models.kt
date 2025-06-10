package com.example.prezzapp.data

import java.io.Serializable

data class Absence(
    val id: String,
    val courseName: String,
    val date: String,
    val professorName: String,
    var isJustified: Boolean,
    val totalStudents: Int = 0,
    val absentCount: Int = 0,
    val justificationLink: String? = null
) : Serializable