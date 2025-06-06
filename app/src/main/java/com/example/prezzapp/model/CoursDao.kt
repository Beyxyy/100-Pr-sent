package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CoursDao {

    @Insert
    fun insert(cours: Cours)

    @Query("DELETE FROM Cours")
    fun deleteAll()

    @Query("SELECT * FROM Cours")
    fun getAll(): List<Cours>

    @Query(
        """
        SELECT DISTINCT u.*
        FROM User u
        INNER JOIN Presence p ON u.id = p.user_id
        WHERE p.cours_id = :coursId AND u.status = :status
        """
    )
    fun getPromobyCours(coursId: Int, status: Status): List<User>
}
