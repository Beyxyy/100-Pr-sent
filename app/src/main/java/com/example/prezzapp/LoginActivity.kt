package com.example.prezzapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.prezzapp.service.AuthService

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.editTextEmail)
        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        val authService = AuthService(this)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            try {
                val user = authService.authenticate(email, password)

                if (user != null) {
                    val role = user.getString("role")
                    val userId = user.getInt("id") // 👈 Récupère l'ID ici
                    Toast.makeText(this, "Bienvenue ${user.getString("nom")}", Toast.LENGTH_SHORT).show()

                    if (role == "enseignant") {
                        startActivity(Intent(this, TeacherDashboardActivity::class.java))
                    } else if (role == "etudiant") {
                        val intent = Intent(this, StudentDashboardActivity::class.java)
                        intent.putExtra("userId", userId) // 👈 Passe l'ID ici
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Rôle inconnu", Toast.LENGTH_SHORT).show()
                    }

                    finish()
                }
                else {
                    Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                    passwordInput.text.clear()
                    passwordInput.requestFocus()
                }

            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                passwordInput.text.clear()
                passwordInput.requestFocus()
            }
        }
    }
}
