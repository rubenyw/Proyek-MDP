package com.example.project

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    fun insert(user:UserEntity)

    @Update
    fun update(user:UserEntity)

    @Delete
    fun delete(user:UserEntity)

    @Query("DELETE FROM users where username = :username")
    fun deleteQuery(username: String):Int

    @Query("SELECT * FROM users")
    fun fetch():List<UserEntity>

    @Query("SELECT * FROM users where username = :username")
    fun get(username:String):UserEntity?

    @Query("SELECT * FROM users where id = :username")
    fun get(username:Int):UserEntity?
}