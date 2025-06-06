package com.example.prezzapp.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("DELETE FROM User")
    fun deleteAll()

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Query("SELECT * FROM User WHERE login = :email AND password = :password LIMIT 1")
    fun getByEmailAndPassword(email: String, password: String): User?
}
