package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao interface CoursDao {
    @Insert fun insert(cours: Cours)
    @Query("SELECT * FROM Cours") fun getAll(): List<Cours>
    @Query("DELETE FROM Cours") fun deleteAll()
    @Query("""
        SELECT user.* FROM Cours JOIN User ON Cours.annee = User.annee AND Cours.spe = User.spe 
        WHERE Cours.id = :coursId AND User.status = :status
    """)
    fun getPromobyCours(coursId: Int, status: Status = Status.STUDENT): List<User>
    @Query("SELECT * FROM Cours WHERE prof = :profNom") fun getCoursByProfName(profNom: String): List<Cours>
    @Query("SELECT * FROM Cours WHERE id = :coursId LIMIT 1") fun getById(coursId: Int): Cours?
}


