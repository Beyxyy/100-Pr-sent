package com.example.prezzapp.service

import androidx.activity.ComponentActivity
import com.example.prezzapp.model.AppDatabase

abstract class Service {

    protected var dbInstance: AppDatabase

    constructor(context: ComponentActivity) {
        dbInstance = AppDatabase.getDatabase(context)
    }
}