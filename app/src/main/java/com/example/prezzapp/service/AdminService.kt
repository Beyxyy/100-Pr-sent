package com.example.prezzapp.service

import androidx.activity.ComponentActivity
import com.example.prezzapp.database.SftpConnection
import com.example.prezzapp.model.Absence
import com.example.prezzapp.model.Cours
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.Status
import com.example.prezzapp.model.User
import com.example.prezzapp.model.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminService : Service {

    private var userDao : UserDao
    private var presenceDao : PresenceDao
    private var coursDao = dbInstance.coursDao()

    constructor(context: ComponentActivity) : super(context) {
        userDao = dbInstance.userDao()
        presenceDao = dbInstance.presenceDao()
    }


    suspend fun getAbsencesNotJustified(): List<Absence> = withContext(Dispatchers.IO) {
        presenceDao.getAllAbsencesUnJustified()
    }

    suspend fun getAbsenceById(id: Int): Absence? = withContext(Dispatchers.IO) {
        presenceDao.getAbsenceByIdAdmin(id)
    }


    suspend fun initData() {
        withContext(Dispatchers.IO) {
            val coursList = listOf(
                Cours(0, "Joel Dion", "01/01/2025", "8h-10h", "CM", "1A", "IR", "Mathématiques"),
                Cours(0, "Luc Bernard", "02/01/2025", "10h-12h", "TP", "1A", "IR", "Physique"),
                Cours(0, "Julie Petit", "03/01/2025", "13h-15h", "TD", "1A", "IR", "Chimie"),
                Cours(0, "Jean Lemoine", "04/01/2025", "15h-17h", "CM", "2A", "IR", "Biologie"),
                Cours(0, "Sophie Marchand", "05/01/2025", "17h-19h", "CM", "3A", "IR", "Sciences Sociales"),
            )
            coursList.forEach { coursDao.insert(it) }


            val users = listOf(
                User(
                    0,
                    "joel.dion@uha.fr",
                    "Joel Dion",
                    "azerty1",
                    "",
                    "",
                    "",
                    "",
                    Status.TEACHER
                ),
                User(
                    0,
                    "jean.lemoine@uha.fr",
                    "Jean Lemoine",
                    "azerty2",
                    "",
                    "",
                    "",
                    "",
                    Status.TEACHER
                ),
                User(
                    0,
                    "luc.bernard@uha.fr",
                    "Luc Bernard",
                    "azerty3",
                    "",
                    "",
                    "",
                    "",
                    Status.TEACHER
                ),
                User(
                    0,
                    "sophie.marchand@uha.fr",
                    "Sophie Marchand",
                    "azerty4",
                    "",
                    "",
                    "",
                    "",
                    Status.TEACHER
                ),
                User(
                    0,
                    "julie.petit@uha.fr",
                    "Julie Petit",
                    "azerty5",
                    "",
                    "",
                    "",
                    "",
                    Status.TEACHER
                ),
                User(
                    0,
                    "amine.martin@uha.fr",
                    "Amine Martin",
                    "pass01",
                    "TD2",
                    "TP3",
                    "1A",
                    "IR",
                    Status.STUDENT
                ),
                User(
                    0,
                    "lina.nguyen@uha.fr",
                    "Lina Nguyen",
                    "pass02",
                    "TD2",
                    "TP3",
                    "2A",
                    "IR",
                    Status.STUDENT
                ),
            )
            users.forEach { userDao.insert(it) }

            val presence =Presence(
                    0,
                    1,
                    1,
                    false,
                    null,
                    false)
            presenceDao.insert(presence)
        }

    }

    suspend fun getUserById(id: Int): User? = withContext(Dispatchers.IO) {
       userDao.getById(id)
    }

    suspend fun getAbsenceByUserId(id: Int): List<Absence> = withContext(Dispatchers.IO) {
        presenceDao.geAbsenceByUserId(id)
    }

    suspend fun justifyAbsence(id: Int) : Unit = withContext(Dispatchers.IO) {
        try{
            val absence = presenceDao.getAbsenceById(id)
            if (absence != null) {
                presenceDao.justifyAbscence(absence.id)
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun downloadJustitf(lien : String, absence : Absence ) : Unit {
        Thread {
            val sftpConnection : SftpConnection = SftpConnection.getInstance()
            var localFileName = absence.user.name + "_" + absence.cours.matiere + "_" + absence.cours.jour +"_"+ absence.cours.heure + ".pdf"
            sftpConnection.downloadFileViaSFTP(localFileName = localFileName, remoteFilePath = lien) { success, message ->
                if (success) {
                    // Handle successful download
                } else {
                    // Handle download failure
                }
            }
        }
    }

    suspend fun addCours(
        nomProf: String,
        spe: String,
        groupe: String,
        heure: String,
        date: String,
        annee: String,
        matiere: String
    ) = withContext(Dispatchers.IO) {
        val cours = Cours(
            id = 0,
            prof = nomProf,
            jour = date,
            heure = heure,
            annee = annee,
            groupe = groupe,
            spe = spe,
            matiere = matiere
        )
        coursDao.insert(cours)
    }

}

