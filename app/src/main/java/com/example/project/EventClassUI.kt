package com.example.project

import com.google.firebase.Timestamp
import com.google.type.DateTime
import java.util.Date

// EventClassUI.kt
data class EventClassUI(
    var id: String = "",
    var name: String = "",
    val date: String = "",
    var location: String = "",
    var description: String = "",
    var urlLink: String = "",
    var donation: Int = 0,
    var participants: Int = 0,
    var creator: String = ""
)
