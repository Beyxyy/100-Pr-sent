package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PresenceDao {

    @Insert
    fun insert(presence: Presence)

    @Query("DELETE FROM Presence")
    fun deleteAll()

    @Query("SELECT * FROM Presence")
    fun getAll(): List<Presence>

    @Query("SELECT * FROM Presence WHERE user_id = :userId")
    fun getByUser(userId: Int): List<Presence>

    @Query("SELECT * FROM Presence WHERE id = :absenceId")
    fun getAbsenceById(absenceId: Int): Presence

    @Query("DELETE FROM Presence WHERE id = :presenceId")
    fun deleteById(presenceId: Int)
}
