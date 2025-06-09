package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query



@Dao
interface PresenceDao {
    @Insert fun insert(presence: Presence)
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
    WHERE estJustifie = 0
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

}
