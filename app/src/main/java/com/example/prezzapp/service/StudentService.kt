package com.example.prezzapp.service

import android.content.Context
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.UserDao

class StudentService(context: Context) {

    private val presenceDao: PresenceDao = AppDatabase.getDatabase(context).presenceDao()
    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()

    fun getAllAbsenceByUserId(userId: Int): List<Presence> {
        return presenceDao.getByUser(userId)
    }

    fun getAbsenceById(absenceId: Int): Presence {
        return presenceDao.getAbsenceById(absenceId)
    }
}
