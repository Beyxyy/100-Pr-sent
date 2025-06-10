package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PresenceDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun insert(presence: Presence)

    @Update
    fun update(presence: Presence)

    @Query("DELETE FROM Presence")
    fun deleteAll()

    @Query("SELECT * FROM Presence")
    fun getAll(): List<Presence>

    @Query("SELECT * FROM Presence WHERE user_id = :userId")
    fun getByUser(userId: Int): List<Presence>

    @Query("SELECT * FROM Presence WHERE id = :absenceId")
    fun getAbsenceById(absenceId: Int): Presence?

    @Query("DELETE FROM Presence WHERE id = :presenceId")
    fun deleteById(presenceId: Int)

    // --- Méthodes supplémentaires ---

    // Récupérer uniquement les absences (présences où est_present = 0)
    @Query("SELECT * FROM Presence WHERE est_present = 0")
    fun getAbsences(): List<Presence>

    // Data class pour récupérer absences avec détails
    data class AbsenceWithDetails(
        val presenceId: Int,
        val userName: String,
        val courseName: String,
        val professorName: String,
        val estJustifie: Boolean,
        val date: String?
    )

    // Requête avec jointure pour récupérer absences + infos utilisateurs et cours
    @Query("""
        SELECT Presence.id AS presenceId,
               User.name AS userName,
               Cours.nomcours AS courseName,
               Cours.prof AS professorName,
               Presence.est_justifie AS estJustifie,
               Cours.jour AS date
        FROM Presence
        INNER JOIN User ON Presence.user_id = User.id
        INNER JOIN Cours ON Presence.cours_id = Cours.id
        WHERE Presence.est_present = 0 AND Presence.user_id = :userId
        ORDER BY Cours.jour DESC
    """)
    fun getAbsencesWithDetailsByUser(userId: Int): List<AbsenceWithDetails>
}
