package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "communities")
data class CommunityEntity(
    val id: String,
    val name: String,
):Serializable