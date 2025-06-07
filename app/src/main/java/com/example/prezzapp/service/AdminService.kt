package com.example.prezzapp.service

import androidx.activity.ComponentActivity
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.UserDao

class AdminService : Service {

    private var userDao : UserDao
    private var presenceDao : PresenceDao

    constructor(context: ComponentActivity) : super(context) {
        userDao = dbInstance.userDao()
        presenceDao = dbInstance.presenceDao()
    }




}