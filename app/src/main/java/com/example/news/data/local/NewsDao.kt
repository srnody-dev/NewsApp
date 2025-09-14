package com.example.news.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsDao {


    @Query("SELECT * FROM articles WHERE topic == :topic ORDER BY publishedAt DESC ")
    fun getAllArticleByTopic(topic: String): Flow<List<ArticleDbModel>>


    @Query("SELECT *  FROM articles WHERE title LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun searchArticle(query:String): Flow<List<ArticleDbModel>>

}