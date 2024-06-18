package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    val id: String,
    val owner: String,
    val date: String,
    val context: String
)