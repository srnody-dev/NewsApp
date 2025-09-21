package com.example.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService { //для загрузки данных из сети

    //8829cd1a455d4bbf880ea12c2ed7466f
//ac932b60a57f4df4b56c6574732beb4d был
    @GET("v2/top-headlines?apiKey=8829cd1a455d4bbf880ea12c2ed7466f")
    suspend fun loadArticles( //для определенного topic
        @Query("category") category: String
    ): NewsResponseDto



    @GET("v2/everything?apiKey=8829cd1a455d4bbf880ea12c2ed7466f")
    suspend fun loadArticlesForQuery( // для поиска
        @Query("q") topic: String,
        @Query("sortBy") sortBy: String="relevancy"
    ): NewsResponseDto


    @GET("v2/top-headlines?apiKey=8829cd1a455d4bbf880ea12c2ed7466f")
    suspend fun loadTopArticles( //для топ новостей
        @Query("category") category: String = "general",
    ): NewsResponseDto

}