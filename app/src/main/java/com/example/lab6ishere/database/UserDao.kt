package com.example.lab6ishere.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE login = :login AND pass = :pass LIMIT 1")
    suspend fun getUser(login: String, pass: String): User?
}