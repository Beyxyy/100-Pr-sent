package com.example.prezzapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AuthService(private val context: Context) {
    private val fileName = "users.json"

    fun login(email: String, password: String): String {
        val users = readUsers()
        println("DEBUG - Email saisi : '$email'")

        val existingUser = users.find {
            it.getString("login").trim() == email.trim()
        }

        return if (existingUser != null) {
            val correctPassword = existingUser.getString("mdp").trim()
            if (correctPassword == password.trim()) {
                "Connexion réussie"
            } else {
                "Mot de passe incorrect"
            }
        } else {
            "Utilisateur non trouvé"
        }
    }

    private fun readUsers(): MutableList<JSONObject> {
        val users = mutableListOf<JSONObject>()
        try {
            val inputStream = context.assets.open(fileName)
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                users.add(jsonArray.getJSONObject(i))
            }

            for (user in users) {
                println("DEBUG - utilisateur trouvé: ${user.getString("login")}, mdp: ${user.getString("mdp")}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return users
    }
}