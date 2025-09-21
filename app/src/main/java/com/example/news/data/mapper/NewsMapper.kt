package com.example.news.data.mapper

import android.util.Log
import com.example.news.data.local.ArticleDbModel
import com.example.news.data.remote.NewsResponseDto
import com.example.news.domain.entity.Article
import java.text.SimpleDateFormat
import java.util.Locale

fun NewsResponseDto.toDbModel(topic: String): List<ArticleDbModel> {
    return try {
        articles.map { it ->

                    ArticleDbModel(
                        title = it.title ?: "No title",
                        url = it.url ?: "",
                        description = it.description ?: "",
                        imageUrl = it.urlToImage ?: "",
                        sourceName = it.source.name ?: "",
                        publishedAt = it.publishedAt.toTimestamp(),
                        content = it.content ?: "",
                        topic = topic
                    )

            }
        } catch (e: Exception) {
            Log.e("Mapper", "Error converting to DB model: ${e.message}")
            emptyList()
        }
    }

fun NewsResponseDto.toDbModelForSearch(query: String): List<ArticleDbModel> {
    return try {
        articles.map { it ->

            ArticleDbModel(
                title = it.title ?: "No title",
                url = it.url ?: "",
                description = it.description ?: "",
                imageUrl = it.urlToImage ?: "",
                sourceName = it.source.name ?: "",
                publishedAt = it.publishedAt.toTimestamp(),
                content = it.content ?: "",
                topic = "search:$query"
            )

        }
    } catch (e: Exception) {
        Log.e("Mapper", "Error converting to DB model: ${e.message}")
        emptyList()
    }
}

    private fun String.toTimestamp(): Long {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()

    }


    fun ArticleDbModel.toEntities(): Article {
        return Article(
            title = title,
            description = description,
            imageUrl = imageUrl,
            sourceName = sourceName,
            publishedAt = publishedAt,
            content = content,
            url = url
        )

    }

    fun List<ArticleDbModel>.toEntities(): List<Article> {
        return map { it.toEntities() }
    }
