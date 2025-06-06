package com.example.prezzapp.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("Delete from User")
    fun deleteAll() : Unit

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    fun getById(userId: Int): User?
}
