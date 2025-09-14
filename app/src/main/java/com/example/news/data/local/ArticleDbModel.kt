package com.example.news.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles"
)
data class ArticleDbModel(
    val title: String,
    val description: String,
    val imageUrl:String?,
    val sourceName:String,
    val publishedAt: Long,
    @PrimaryKey
    val url: String,
    val topic: String


    )
