package com.example.prezzapp.service

import android.content.Context
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.User

class AuthService(context: Context) {

    private val userDao = AppDatabase.getDatabase(context).userDao()

    fun authenticate(email: String, password: String): User? {
        return userDao.getAll().firstOrNull { it.login == email && it.password == password }
    }
}
