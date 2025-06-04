package com.example.prezzapp.model

import androidx.room.*

@Dao
interface UserDao {
    @Insert fun insert(user: User)
    @Query("SELECT * FROM User") fun getAll(): List<User>
}
