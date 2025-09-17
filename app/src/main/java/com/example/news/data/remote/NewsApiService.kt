package com.example.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService { //для загрузки данных из сети


    @GET("v2/top-headlines?apiKey=ac932b60a57f4df4b56c6574732beb4d")
    suspend fun loadArticles( //для определенного topic
        @Query("category") category: String //это должно быть только для query и возможно Topic ALL
    ): NewsResponseDto



    @GET("v2/everything?apiKey=ac932b60a57f4df4b56c6574732beb4d")
    suspend fun loadArticlesForQuery( // для поиска
        @Query("q") topic: String,
        @Query("sortBy") sortBy: String="relevancy"
    ): NewsResponseDto


    @GET("v2/top-headlines?apiKey=ac932b60a57f4df4b56c6574732beb4d")
    suspend fun loadTopArticles( //для топ новостей
        @Query("category") category: String = "general",
    ): NewsResponseDto

}