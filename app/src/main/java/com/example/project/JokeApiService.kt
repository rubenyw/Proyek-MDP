package com.example.project
import retrofit2.http.GET
interface JokeApiService {
    @GET("joke/Programming")
    suspend fun getJoke(): JokeResponse
}