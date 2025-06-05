package com.example.prezzapp.service

import androidx.activity.ComponentActivity
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.UserDao


class StudentService : Service {
    private var userDao : UserDao? = null
    private var presenceDao : PresenceDao? = null

    constructor(context: ComponentActivity) : super(context) {
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