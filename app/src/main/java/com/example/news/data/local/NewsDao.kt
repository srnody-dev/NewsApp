package com.example.news.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsDao {

    @Query("SELECT * FROM articles WHERE topic = :topicName ORDER BY publishedAt DESC")
    fun getArticlesByTopic(topicName: String): Flow<List<ArticleDbModel>>


    @Query("SELECT * FROM ARTICLES WHERE url = :url")
    fun getArticle(url: String):Flow<ArticleDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticles(articles: List<ArticleDbModel>): List<Long>

    @Query("DELETE FROM articles WHERE topic IN (:topic)")
    suspend fun deleteArticlesByTopics(topic: String)


    @Query("SELECT *  FROM articles WHERE title LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun searchArticle(query:String): Flow<List<ArticleDbModel>>

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun getPopularArticles(): Flow<List<ArticleDbModel>>



}