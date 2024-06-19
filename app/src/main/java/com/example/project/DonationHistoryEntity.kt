package com.example.project

data class DonationHistoryEntity(
    val ammount: String = "",
    val eventId: String = "",
    val message: String = "",
    val userId: String = ""
) {
    constructor() : this("", "", "", "")
}
