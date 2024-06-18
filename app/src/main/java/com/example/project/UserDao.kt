package com.example.project

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    fun update(user:UserEntity)

    @Delete
    fun delete(user:UserEntity)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Query("SELECT * FROM user")
    fun fetch():List<UserEntity>
}