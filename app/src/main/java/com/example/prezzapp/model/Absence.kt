package com.example.prezzapp.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class Absence(
    @ColumnInfo(name = "presence_id") // Correspond à l'alias dans le SELECT
    val id: Int,

    @ColumnInfo(name = "presence_table_cours_id") // ID du cours venant de la table Presence
    val originalCoursId: Int,

    @ColumnInfo(name = "presence_table_user_id") // ID de l'user venant de la table Presence
    val originalUserId: Int,

    @ColumnInfo(name = "presence_est_justifie")
    val estJustifie: Boolean,

    @ColumnInfo(name = "presence_lien")
    val lien: String?,

    @ColumnInfo(name = "presence_est_present")
    val estPresent: Boolean,

    @Embedded(prefix = "user_") // Le préfixe doit correspondre aux alias dans le SELECT
    val user: User,

    @Embedded(prefix = "cours_") // Le préfixe doit correspondre aux alias dans le SELECT
    val cours: Cours
)