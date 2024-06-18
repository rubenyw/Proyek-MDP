package com.example.project

import com.google.type.DateTime
import java.util.Date

data class EventClass(
    var id: String,
    var name: String,
    var date: String,
    var location: String,
    var description: String,
) {
    constructor() : this("", "", "", "", "") // Default constructor
}