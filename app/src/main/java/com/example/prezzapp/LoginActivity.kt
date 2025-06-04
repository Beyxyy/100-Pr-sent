package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etIdentifier: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Assure-toi que ce layout existe

        etIdentifier = findViewById(R.id.et_identifier)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val identifier = etIdentifier.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Simulation d'authentification
            if (identifier == "eleve123" && password == "password") {
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, StudentDashboardActivity::class.java)
                startActivity(intent)
                finish() // Empêche de revenir à l'écran de connexion avec le bouton retour
            } else {
                Toast.makeText(this, "Identifiant ou mot de passe incorrect.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
