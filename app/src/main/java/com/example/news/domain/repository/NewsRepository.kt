package com.example.news.domain.repository

import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import kotlinx.coroutines.flow.Flow

interface NewsRepository {


    suspend fun searchArticle(query:String): Flow<List<Article>>

    fun getTopArticles():Flow<List<Article>>

    suspend fun updateTopArticles()



    suspend fun removeArticlesForTopic(topic: String)

    suspend fun updateArticlesForTopic(topic: String) //обновить статьи для определенной темы topic-тема

    fun getArticleByTopic(topic: Topic):Flow<List<Article>> // типо возврщает список статей для определенных тем

    suspend fun updateArticlesForAllTopic() //обновить статьи для всех подписок,можно реализовать через свайп вниз



}