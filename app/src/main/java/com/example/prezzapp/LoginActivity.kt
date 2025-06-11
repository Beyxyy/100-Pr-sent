package com.example.prezzapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prezzapp.databinding.ActivityLoginBinding
import com.example.prezzapp.model.*
import com.example.prezzapp.service.AuthService

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val savedUserId = sharedPref.getInt("user_id", -1)
        val savedStatus = sharedPref.getString("status", null)

        if (savedUserId != -1 && savedStatus != null) {
            Thread {
                val db = AppDatabase.getDatabase(this)
                val login = db.userDao().getAll().find { it.id == savedUserId }?.login ?: ""
                runOnUiThread {
                    val intent = when (Status.valueOf(savedStatus)) {
                        Status.TEACHER -> Intent(this, TeacherDashboardActivity::class.java).apply {
                            putExtra("prof_login", login)
                        }
                        Status.STUDENT -> Intent(this, StudentDashboardActivity::class.java).apply {
                            putExtra("user_id", savedUserId)
                        }
                        Status.ADMIN -> Intent(this, AdminActivity::class.java).apply {
                            putExtra("user_id", savedUserId)
                        }
                    }
                    startActivity(intent)
                    finish()
                }
            }.start()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailInput = binding.editTextEmail
        val passwordInput = binding.editTextPassword
        val loginButton = binding.buttonLogin
        val themeButton = binding.btnToggleTheme

        val db = AppDatabase.getDatabase(this)
        val authService = AuthService(this)

        Thread {
            val coursDao = db.coursDao()
            val userDao = db.userDao()
            val presenceDao = db.presenceDao()

            if (userDao.getAll().isEmpty()) {
                coursDao.deleteAll()
                userDao.deleteAll()
                presenceDao.deleteAll()

                val coursList = listOf(
                    Cours(0, "Joel Dion", "Stats", "11/06/2025", "8h-10h", "CM", "1A", "IR", "Stats"),
                    Cours(0, "Joel Dion", "Calcul Matriciel", "12/06/2025", "8h-10h", "CM", "1A", "IR", "Stats"),

                    )
                coursList.forEach { coursDao.insert(it) }

                val allCours = coursDao.getAll()

                val users = listOf(
                    User(0, "joel.dion@uha.fr", "Joel Dion", "azerty1", "", "", "", "", Status.TEACHER),
                    User(0, "julie.petit@uha.fr", "Julie Petit", "azerty5", "", "", "", "", Status.ADMIN),
                    User(0, "amine.martin@uha.fr", "Amine Martin", "pass01", "TD2", "TP3", "1A", "IR", Status.STUDENT),
                    User(0, "youssef.delgrande@uha.fr", "Youssef Delgrande", "pass01", "TD1", "TP1", "1A", "IR", Status.STUDENT),
                    User(0, "thomas.becher@uha.fr", "Thomas Bécher", "pass01", "TD1", "TP2", "1A", "IR", Status.STUDENT)

                )
                users.forEach { userDao.insert(it) }

                val amine = users.first { it.name == "Amine Martin" }
                val coursStats = allCours.first { it.nomcours=="Stats" }

                 presenceDao.insert(Presence(0, amine.id, coursStats.id, false, null, false))
                 presenceDao.insert(Presence(0, amine.id, coursStats.id, false, null, false))
                presenceDao.insert(Presence(0, amine.id, coursStats.id, true, "justificatif.pdf", false))


                val joelCourses = allCours.filter { it.prof == "Joel Dion" && it.annee == "1A" && it.spe == "IR" }
                val firstYearStudents = users.filter { it.status == Status.STUDENT && it.annee == "1A" && it.spe == "IR" }

                firstYearStudents.forEach { student ->
                    joelCourses.forEach { course ->
                        val alreadyExists = presenceDao.getAll().any { it.userId == student.id && it.coursId == course.id }
                        if (!alreadyExists) {
                            presenceDao.insert(Presence(0, student.id, course.id, false, null, true))
                        }
                    }
                }
            }
        }.start()

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            Thread {
                val user = authService.authenticate(email, password)

                runOnUiThread {
                    if (user != null) {
                        saveLoggedInUser(user.id, user.status)
                        Toast.makeText(this, "Bienvenue ${user.name}", Toast.LENGTH_SHORT).show()
                        val intent = when (user.status) {
                            Status.TEACHER -> Intent(this, TeacherDashboardActivity::class.java).apply {
                                putExtra("prof_login", user.login)
                            }
                            Status.STUDENT -> Intent(this, StudentDashboardActivity::class.java).apply {
                                putExtra("user_id", user.id)
                            }
                            Status.ADMIN -> Intent(this, AdminActivity::class.java).apply {
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

        themeButton.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }
    }

    private fun saveLoggedInUser(userId: Int, status: Status) {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("user_id", userId)
            putString("status", status.name)
            apply()
        }
    }
}
