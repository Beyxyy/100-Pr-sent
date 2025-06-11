package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PresenceDao {
    @Insert fun insert(presence: Presence)
    @Query("""
    SELECT 
        presence.id, presence.user_id, presence.cours_id, presence.estJustifie, presence.lien, presence.estPresent,
        user.id AS user_id, user.login AS user_login, user.name AS user_name, user.password AS user_password, 
        user.td AS user_td, user.tp AS user_tp, user.annee AS user_annee, user.spe AS user_spe, user.status AS user_status,
        cours.id AS cours_id, cours.prof AS cours_prof, cours.jour AS cours_jour, cours.heure AS cours_heure, 
        cours.groupe AS cours_groupe, cours.annee AS cours_annee, cours.spe AS cours_spe, cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.userId
    JOIN cours ON cours.id = presence.coursId
    WHERE estJustifie = 0 and lien IS NOT NULL
""")fun getAllAbsencesUnJustified(): List<Absence>

    @Query("SELECT * FROM Presence WHERE id=:id") fun getAbsenceById(id: Int): Presence

    @Query("SELECT * FROM Presence WHERE userId = :userId") fun getByUser(userId: Int): List<Presence>

    @Query("""
    SELECT 
        presence.id, presence.userId, presence.coursId, presence.estJustifie, presence.lien, presence.estPresent,
        user.id AS user_id, user.login AS user_login, user.name AS user_name, user.password AS user_password, 
        user.td AS user_td, user.tp AS user_tp, user.annee AS user_annee, user.spe AS user_spe, user.status AS user_status,
        cours.id AS cours_id, cours.prof AS cours_prof, cours.jour AS cours_jour, cours.heure AS cours_heure, 
        cours.groupe AS cours_groupe, cours.annee AS cours_annee, cours.spe AS cours_spe, cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.userId
    JOIN cours ON cours.id = presence.coursId
    WHERE presence.id = :id
""") fun getAbsenceByIdAdmin(id : Int) : Absence?
    @Query("""
    SELECT
    presence.id, presence.userId, presence.coursId, presence.estJustifie, presence.lien, presence.estPresent,
    user.id AS user_id, user.login AS user_login, user.name AS user_name, user.password AS user_password,
    user.td AS user_td, user.tp AS user_tp, user.annee AS user_annee, user.spe AS user_spe, user.status AS user_status,
    cours.id AS cours_id, cours.prof AS cours_prof, cours.jour AS cours_jour, cours.heure AS cours_heure,
    cours.groupe AS cours_groupe, cours.annee AS cours_annee, cours.spe AS cours_spe, cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.userId
    JOIN cours ON cours.id = presence.coursId
    WHERE user.id = :id
    """)
    fun geAbsenceByUserId(id : Int) :  List<Absence>

    @Query("Update Presence SET estJustifie = true WHERE id = :id")
    fun justifyAbscence(id: Int)


    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun insert(presence: Presence)
    @Update fun update(presence: Presence)
    @Query("DELETE FROM Presence") fun deleteAll()
    @Query("SELECT * FROM Presence") fun getAll(): List<Presence>
    @Query("SELECT * FROM Presence WHERE user_id = :userId") fun getByUser(userId: Int): List<Presence>
    @Query("SELECT * FROM Presence WHERE id = :absenceId") fun getAbsenceById(absenceId: Int): Presence?
    @Query("DELETE FROM Presence WHERE id = :presenceId") fun deleteById(presenceId: Int)
    @Query("SELECT * FROM Presence WHERE est_present = 0") fun getAbsences(): List<Presence>

    data class AbsenceWithDetails(
        val presenceId: Int,
        val userName: String,
        val courseName: String,
        val professorName: String,
        val estJustifie: Boolean,
        val date: String?
    )

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
