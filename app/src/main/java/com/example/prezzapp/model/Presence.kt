package com.example.prezzapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["user_id"]),
        ForeignKey(entity = Cours::class, parentColumns = ["id"], childColumns = ["cours_id"])
    ]
)
data class Presence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "cours_id") val coursId: Int,
    @ColumnInfo(name = "est_justifie") val estJustifie: Boolean,
    @ColumnInfo(name = "lien") val lien: String?,
    @ColumnInfo(name = "est_present") val estPresent: Boolean
    /*val userId: Int,
    val coursId: Int,
    var estJustifie: Boolean,
    val lien: String?,
    val estPresent: Boolean*/
)
