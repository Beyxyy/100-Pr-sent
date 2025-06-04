package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CoursDao {
    @Insert fun insert(cours: Cours)
    @Query("SELECT * FROM Cours") fun getAll(): List<Cours>
}
