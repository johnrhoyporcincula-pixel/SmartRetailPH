package com.example.smartretailph.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartretailph.data.local.entities.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)
}

