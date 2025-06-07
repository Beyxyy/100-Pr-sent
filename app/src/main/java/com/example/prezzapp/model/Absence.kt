package com.example.prezzapp.model

import androidx.room.Embedded

data class Absence(
    @Embedded val presence: Presence,
    @Embedded(prefix = "user_") val user: User,
    @Embedded(prefix = "cours_") val cours: Cours
)