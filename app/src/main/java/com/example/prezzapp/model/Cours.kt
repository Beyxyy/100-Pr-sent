package com.example.prezzapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cours(
    @PrimaryKey val id: Int,
    val prof: String
)
