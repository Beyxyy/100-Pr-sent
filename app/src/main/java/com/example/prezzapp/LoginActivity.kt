package com.example.prezzapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.prezzapp.model.*
import com.example.prezzapp.service.AuthService

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.editTextEmail)
        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val loginButton  = findViewById<Button>(R.id.buttonLogin)

        val db          = AppDatabase.getDatabase(this)
        val authService = AuthService(this)

        Thread {
            val coursDao    = db.coursDao()
            val userDao     = db.userDao()
            val presenceDao = db.presenceDao()

            if (userDao.getAll().isEmpty()) {
                coursDao.deleteAll()
                userDao.deleteAll()
                presenceDao.deleteAll()

                val coursList = listOf(
                    Cours(0, "Joel Dion",      "Mathématiques", "01/01/2025", "8h-10h",  "CM", "1A", "IR"),
                    Cours(0, "Luc Bernard",    "Informatique",  "02/01/2025", "10h-12h", "TP", "1A", "IR"),
                    Cours(0, "Julie Petit",    "Physique",      "03/01/2025", "13h-15h", "TD", "1A", "IR"),
                    Cours(0, "Jean Lemoine",   "Chimie",        "04/01/2025", "15h-17h", "CM", "2A", "IR"),
                    Cours(0, "Sophie Marchand","Biologie",      "05/01/2025", "17h-19h", "CM", "3A", "IR")
                )
                coursList.forEach { coursDao.insert(it) }

                val allCours = coursDao.getAll()

                val users = listOf(
                    User(101,"joel.dion@uha.fr",     "Joel Dion",      "azerty1", "", "", "", "", Status.TEACHER),
                    User(102,"jean.lemoine@uha.fr",  "Jean Lemoine",   "azerty2", "", "", "", "", Status.TEACHER),
                    User(103,"luc.bernard@uha.fr",   "Luc Bernard",    "azerty3", "", "", "", "", Status.TEACHER),
                    User(104,"sophie.marchand@uha.fr","Sophie Marchand","azerty4", "", "", "", "", Status.TEACHER),
                    User(105,"julie.petit@uha.fr",   "Julie Petit",    "azerty5", "", "", "", "", Status.TEACHER),
                    User(1,  "amine.martin@uha.fr",  "Amine Martin",   "pass01", "TD2", "TP3", "1A", "IR", Status.STUDENT),
                    User(2,  "lina.nguyen@uha.fr",   "Lina Nguyen",    "pass02", "TD2", "TP3", "2A", "IR", Status.STUDENT),
                    User(3,  "youssef.diallo@uha.fr","Youssef Diallo", "pass03", "TD2", "TP2", "1A", "IR", Status.STUDENT),
                    User(4,  "nina.morel@uha.fr",    "Nina Morel",     "pass04", "TD1", "TP3", "3A", "IR", Status.STUDENT)
                )
                users.forEach { userDao.insert(it) }

                val amine       = users.first { it.name == "Amine Martin" }
                val coursAmine  = allCours.filter { it.annee == "1A" && it.spe == "IR" }

                val presenceList = listOfNotNull(
                    coursAmine.find { it.prof == "Joel Dion"   }?.let { Presence(0, amine.id, it.id, false, null, false) },
                    coursAmine.find { it.prof == "Luc Bernard" }?.let { Presence(0, amine.id, it.id, false, null, false) },
                    coursAmine.find { it.prof == "Julie Petit" }?.let { Presence(0, amine.id, it.id, true,  "justificatif.pdf", false) }
                )
                presenceList.forEach { presenceDao.insert(it) }

                val joelCourse = allCours.first { it.prof == "Joel Dion" && it.annee == "1A" && it.spe == "IR" }
                val firstYearStudents = users.filter { it.status == Status.STUDENT && it.annee == "1A" && it.spe == "IR" }

                firstYearStudents.forEach { student ->
                    if (presenceDao.getAll().none { it.userId == student.id && it.coursId == joelCourse.id }) {
                        presenceDao.insert(Presence(0, student.id, joelCourse.id, false, null, true))
                    }
                }
            }
        }.start()

        loginButton.setOnClickListener {
            val email    = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            Thread {
                val user = authService.authenticate(email, password)

                runOnUiThread {
                    if (user != null) {
                        Toast.makeText(this, "Bienvenue ${user.name}", Toast.LENGTH_SHORT).show()
                        val intent = when (user.status) {
                            Status.TEACHER -> Intent(this, TeacherDashboardActivity::class.java).apply {
                                putExtra("prof_login", user.login)
                            }
                            Status.STUDENT -> Intent(this, StudentDashboardActivity::class.java).apply {
                                putExtra("user_id", user.id)
                            }
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                        passwordInput.text.clear()
                        passwordInput.requestFocus()
                    }
                }
            }.start()
        }
    }
}
