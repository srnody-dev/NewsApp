package com.example.news.domain.repository

import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Url

interface NewsRepository {
    fun getArticlesForQuery(query: String):Flow<List<Article>>

    fun getArticle(url: String) :Flow<Article>

    suspend fun updateArticlesForQuery(query: String)

    fun getTopArticles():Flow<List<Article>>

    suspend fun updateTopArticles()


    suspend fun updateArticlesForTopic(topic: String) //обновить статьи для определенной темы topic-тема

    fun getArticleByTopic(topic: Topic):Flow<List<Article>> // возврщает список статей для определенных тем

    suspend fun updateArticlesForAllTopic() //обновить статьи для всех подписок,можно реализовать через свайп вниз

}