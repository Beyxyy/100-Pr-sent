package com.example.prezzapp.model

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStatus(value: Status): String {
        return value.name  // convertit l'enum en String
    }

    @TypeConverter
    fun toStatus(value: String): Status {
        return Status.valueOf(value)  // convertit le String en enum
    }
}
