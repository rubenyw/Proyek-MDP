package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "communities")
data class CommunityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)