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

    suspend fun searchStudentsByName(query: String): List<User> {
        return withContext(Dispatchers.IO) {
            // Implement actual search logic using your DAO
            // Example using Room:
            userDao.searchByName("%$query%")
        }
    }


    suspend fun getAbsencesNotJustified(): List<Absence> = withContext(Dispatchers.IO) {
        presenceDao.getAllAbsencesUnJustified()
    }

    suspend fun getAbsenceById(id: Int): Absence? = withContext(Dispatchers.IO) {
        presenceDao.getAbsenceByIdAdmin(id)
    }


    suspend fun initData() {
        withContext(Dispatchers.IO) {
            val coursList = emptyList<Cours>()
            coursList.forEach { coursDao.insert(it) }


            val users = emptyList<User>()
            users.forEach { userDao.insert(it) }

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
            matiere = matiere,
            nomcours = matiere
        )
        coursDao.insert(cours)
    }

}

