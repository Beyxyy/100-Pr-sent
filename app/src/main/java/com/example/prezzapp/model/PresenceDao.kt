package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PresenceDao {
    @Insert fun insert(presence: Presence)
    @Query("SELECT * from Presence where id= :presenceId ") fun getAbsenceById(presenceId : Int) : Presence
    @Query("SELECT * FROM Presence WHERE userId = :userId") fun getByUser(userId: Int): List<Presence>
}
