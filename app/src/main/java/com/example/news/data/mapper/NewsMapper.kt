package com.example.news.data.mapper

import com.example.news.data.local.ArticleDbModel
import com.example.news.data.remote.NewsResponseDto
import com.example.news.domain.entity.Article
import java.text.SimpleDateFormat
import java.util.Locale

fun NewsResponseDto.toDbModel(topic: String): List<ArticleDbModel> {
    return articles.map {
        ArticleDbModel(
            title = it.title,
            url = it.title,
            description = it.description,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            publishedAt = it.publishedAt.toTimestamp(),
            topic = topic
        )
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
        url = url
    )

}

fun List<ArticleDbModel>.toEntities(): List<Article> {
    return map { it.toEntities() }
}
