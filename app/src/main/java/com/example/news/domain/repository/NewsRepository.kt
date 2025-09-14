package com.example.news.domain.repository

import com.example.news.domain.entity.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun searchArticle(query:String): Flow<List<Article>>

    //fun getArticle(noteId:Int): Flow<ArticleDto>

    fun getAllArticle(): Flow<List<Article>>

    fun getLatestArticles():Flow<List<Article>>


    suspend fun updateArticlesForTopic(topic: String) //обновить статьи для определенной темы topic-тема

    fun getArticleByTopic(topic: String):Flow<List<Article>> //??? типо возврщает список статей для определенных тем

    suspend fun updateArticlesForAllSubscription() //обновить статьи для всех подписок,можно реализовать через свайп вниз



}