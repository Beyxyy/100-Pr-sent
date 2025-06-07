package com.example.prezzapp.service

import androidx.activity.ComponentActivity
import androidx.annotation.WorkerThread
import com.example.prezzapp.model.Absence
import com.example.prezzapp.model.Cours
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.Status
import com.example.prezzapp.model.User
import com.example.prezzapp.model.UserDao
import kotlin.concurrent.thread
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

    @WorkerThread
    suspend fun getAbsencesNotJustified(): List<Absence> = withContext(Dispatchers.IO) {
        presenceDao.getAllAbsencesUnJustified()
    }

    @WorkerThread
    suspend fun initData() {
        withContext(Dispatchers.IO) {
            val coursList = listOf(
                Cours(0, "Joel Dion", "01/01/2025", "8h-10h", "CM", "1A", "IR"),
                Cours(0, "Luc Bernard", "02/01/2025", "10h-12h", "TP", "1A", "IR"),
                Cours(0, "Julie Petit", "03/01/2025", "13h-15h", "TD", "1A", "IR"),
                Cours(0, "Jean Lemoine", "04/01/2025", "15h-17h", "CM", "2A", "IR"),
                Cours(0, "Sophie Marchand", "05/01/2025", "17h-19h", "CM", "3A", "IR")
            )
            coursList.forEach { coursDao.insert(it) }

            val allCours = coursDao.getAll()

            val users = listOf(
                User(
                    101,
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
                    102,
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
                    103,
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
                    104,
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
                    105,
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
                    1,
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
                    2,
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
        }
    }
}

