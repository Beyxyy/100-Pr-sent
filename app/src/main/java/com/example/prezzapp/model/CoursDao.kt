package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CoursDao {
    @Insert fun insert(cours: Cours)
    @Query("SELECT * FROM Cours") fun getAll(): List<Cours>
    @Query("DELETE FROM Cours") fun deleteAll(): Unit
    @Query("Select user.* from cours join user on cours.annee=user.annee and cours.spe=user.spe where cours.id= :coursId and user.status='student'") fun getPromobyCours(coursId : String ): List<User>
}
