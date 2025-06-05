package com.example.prezzapp.service

import androidx.activity.ComponentActivity
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.UserDao


class StudentService {
    private var dbInstance: AppDatabase? = null
    private var userDao : UserDao? = null
    private var presenceDao : PresenceDao? = null

    constructor(context: ComponentActivity) {
        dbInstance = AppDatabase.getDatabase(context);
        userDao = dbInstance!!.userDao()
        presenceDao = dbInstance!!.presenceDao()
    }


    fun getAllAbsenceByUserId(userId : Int) : List<Presence>{
        return presenceDao!!.getByUser(userId)
    }

    fun getAbsenceById(absenceId : Int) : Presence {
        return presenceDao!!.getAbsenceById(absenceId)
    }


}