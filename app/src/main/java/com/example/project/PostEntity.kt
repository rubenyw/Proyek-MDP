package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val owner: String,
    val date: String,
    val context: String
)