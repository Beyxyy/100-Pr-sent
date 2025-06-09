package com.example.prezzapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(entity = Cours::class, parentColumns = ["id"], childColumns = ["coursId"])
    ]
)
data class Presence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val coursId: Int,
    var estJustifie: Boolean,
    val lien: String?,
    val estPresent: Boolean
)
