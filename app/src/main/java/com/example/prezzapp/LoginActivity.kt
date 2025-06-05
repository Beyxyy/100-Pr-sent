package com.example.prezzapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

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

            val result = authService.login(email, password)

            if (result == "Connexion réussie") {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, StudentDashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                passwordInput.text.clear()
                passwordInput.requestFocus()
            }
        }
    }
}
