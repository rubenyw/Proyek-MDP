package com.example.project

import com.google.firebase.Timestamp
import com.google.type.DateTime
import java.util.Date

data class EventClass(
    var id: String,
    var name: String,
    val date: Timestamp,
    var location: String,
    var description: String,
    var urlLink: String,
)