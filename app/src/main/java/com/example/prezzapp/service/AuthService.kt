package com.example.prezzapp.service

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AuthService(private val context: Context) {
    private val fileName = "users.json"

    private fun readUsers(): List<JSONObject> {
        val users = mutableListOf<JSONObject>()
        try {
            val inputStream = context.assets.open(fileName)
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                users.add(jsonArray.getJSONObject(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return users
    }

    fun authenticate(email: String, password: String): JSONObject? {
        val users = readUsers()

        val user = users.find {
            it.getString("login").trim().lowercase() == email.trim().lowercase()
        }

        return if (user != null) {
            if (user.getString("mdp").trim() == password.trim()) {
                user
            } else {
                throw IllegalArgumentException("Mot de passe incorrect")
            }
        } else {
            null
        }
    }

}
