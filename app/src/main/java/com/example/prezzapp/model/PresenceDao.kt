package com.example.prezzapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao interface PresenceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) fun insert(presence: Presence)
    @Query("SELECT * from Presence where id = :presenceId") fun getAbsenceById(presenceId: Int): Presence
    @Query("SELECT * FROM Presence WHERE userId = :userId") fun getByUser(userId: Int): List<Presence>
    @Query("DELETE FROM Presence") fun deleteAll()
    @Update fun updatePresence(presence: Presence)
}

