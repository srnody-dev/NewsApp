package com.example.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService { //для загрузки данных из сети


    @GET("v2/everything?apiKey=ac932b60a57f4df4b56c6574732beb4d")
    suspend fun loadArticles(
        @Query("q") topic: String
    ): NewsResponseDto

    @GET("v2/everything?apiKey=ac932b60a57f4df4b56c6574732beb4d")
    suspend fun getTopArticles(
        @Query("sortBy") sortBy: String = "popularity",
    ): NewsResponseDto

}