package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PresenceDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) fun insert(presence: Presence)
    @Query("""
    SELECT 
        presence.id as presence_id, 
        presence.cours_id as presence_table_cours_id,  -- ID du cours venant de la table Presence
        presence.user_id as presence_table_user_id,    -- ID de l'user venant de la table Presence
        presence.est_justifie as presence_est_justifie, 
        presence.lien as presence_lien, 
        presence.est_present as presence_est_present,
        
        user.id AS user_id, 
        user.login AS user_login, 
        user.name AS user_name, 
        user.password AS user_password, 
        user.td AS user_td, 
        user.tp AS user_tp, 
        user.annee AS user_annee, 
        user.spe AS user_spe, 
        user.status AS user_status,
        
        cours.id AS cours_id, 
        cours.prof AS cours_prof, 
        cours.nomcours AS cours_nomcours, -- AJOUTÉ SELON LE MESSAGE D'ERREUR
        cours.jour AS cours_jour, 
        cours.heure AS cours_heure, 
        cours.groupe AS cours_groupe, 
        cours.annee AS cours_annee, 
        cours.spe AS cours_spe, 
        cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.user_id
    JOIN cours ON cours.id = presence.cours_id
    WHERE presence.est_justifie = 0
""") // J'ai aussi préfixé est_justifie et lien avec presence. pour être explicite
    fun getAllAbsencesUnJustified(): List<Absence>


    @Query("SELECT * FROM Presence WHERE id=:id") fun getAbsenceById(id: Int): Presence

    @Query("SELECT * FROM Presence WHERE user_id = :userId") fun getByUser(userId: Int): List<Presence>

    @Query("""
    SELECT 
        presence.id as presence_id,                      -- Pour Absence.id
        presence.cours_id as presence_table_cours_id,    -- Pour Absence.originalCoursId
        presence.user_id as presence_table_user_id,      -- Pour Absence.originalUserId
        presence.est_justifie as presence_est_justifie,  -- Pour Absence.estJustifie
        presence.lien as presence_lien,                  -- Pour Absence.lien (si toujours nécessaire)
        presence.est_present as presence_est_present,    -- Pour Absence.estPresent
        
        user.id AS user_id, 
        user.login AS user_login, 
        user.name AS user_name, 
        user.password AS user_password, 
        user.td AS user_td, 
        user.tp AS user_tp, 
        user.annee AS user_annee, 
        user.spe AS user_spe, 
        user.status AS user_status,
        
        cours.id AS cours_id, 
        cours.prof AS cours_prof, 
        cours.nomcours AS cours_nomcours,  -- Pour Cours.nomcours (via @Embedded)
        cours.jour AS cours_jour, 
        cours.heure AS cours_heure, 
        cours.groupe AS cours_groupe, 
        cours.annee AS cours_annee, 
        cours.spe AS cours_spe, 
        cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.user_id
    JOIN cours ON cours.id = presence.cours_id
    WHERE presence.id = :id 
""")
    fun getAbsenceByIdAdmin(id : Int) : Absence? // Cette requête devra peut-être aussi être ajustée si elle utilise la même classe Absence

    @Query("""
    SELECT 
        presence.id as presence_id, 
        presence.cours_id as presence_table_cours_id, 
        presence.user_id as presence_table_user_id, 
        presence.est_justifie as presence_est_justifie, 
        presence.lien as presence_lien, 
        presence.est_present as presence_est_present,
        
        user.id AS user_id, 
        user.login AS user_login, 
        user.name AS user_name, 
        user.password AS user_password, 
        user.td AS user_td, 
        user.tp AS user_tp, 
        user.annee AS user_annee, 
        user.spe AS user_spe, 
        user.status AS user_status,
        
        cours.id AS cours_id, 
        cours.prof AS cours_prof, 
        cours.nomcours AS cours_nomcours,
        cours.jour AS cours_jour, 
        cours.heure AS cours_heure, 
        cours.groupe AS cours_groupe, 
        cours.annee AS cours_annee, 
        cours.spe AS cours_spe, 
        cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.user_id
    JOIN cours ON cours.id = presence.cours_id
    WHERE presence.user_id = :id  -- Condition WHERE modifiée pour correspondre au paramètre
""")
    fun geAbsenceByUserId(id : Int) :  List<Absence>

    @Query("Update Presence SET est_justifie = true WHERE id = :id")
    fun justifyAbscence(id: Int)


    @Update fun update(presence: Presence)
    @Query("DELETE FROM Presence") fun deleteAll()
    @Query("SELECT * FROM Presence") fun getAll(): List<Presence>
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

    @Query("""
    SELECT 
        presence.id as presence_id, 
        presence.cours_id as presence_table_cours_id, 
        presence.user_id as presence_table_user_id, 
        presence.est_justifie as presence_est_justifie, 
        presence.lien as presence_lien, 
        presence.est_present as presence_est_present,
        
        user.id AS user_id, 
        user.login AS user_login, 
        user.name AS user_name, 
        user.password AS user_password, 
        user.td AS user_td, 
        user.tp AS user_tp, 
        user.annee AS user_annee, 
        user.spe AS user_spe, 
        user.status AS user_status,
        
        cours.id AS cours_id, 
        cours.prof AS cours_prof, 
        cours.nomcours AS cours_nomcours,
        cours.jour AS cours_jour, 
        cours.heure AS cours_heure, 
        cours.groupe AS cours_groupe, 
        cours.annee AS cours_annee, 
        cours.spe AS cours_spe, 
        cours.matiere AS cours_matiere
    FROM Presence
    JOIN user ON user.id = presence.user_id
    JOIN cours ON cours.id = presence.cours_id
    WHERE cours.matiere = :matiere and presence.est_justifie=false  -- Condition WHERE modifiée pour correspondre au paramètre
""")
   fun getAbsenceByMatiere(matiere : String) : List<Absence>

}
