package com.example.project
import com.squareup.moshi.Json

data class JokeResponse(
    @Json(name = "error") val error: Boolean,
    @Json(name = "joke") val joke: String?
)
