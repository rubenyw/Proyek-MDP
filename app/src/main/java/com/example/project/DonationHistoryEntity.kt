package com.example.project

import com.google.firebase.Timestamp

data class DonationHistoryEntity(
    val ammount: String = "",
    val eventId: String = "",
    val message: String = "",
    val userId: String = "",
    val time: Timestamp,
) {
    constructor() : this("", "", "", "", Timestamp.now())
}
