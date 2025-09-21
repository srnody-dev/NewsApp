package com.example.news.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ArticleDto ( //класс предоставляющий собой статью


    @SerialName("description")
    val description: String ="",

    @SerialName("content")
    val content: String = "",

    @SerialName("publishedAt")
    val publishedAt: String = "",
    @SerialName("source")
    val source: SourceDto= SourceDto(),
    @SerialName("title")
    val title: String = "",
    @SerialName("url")
    val url: String ="",
    @SerialName("urlToImage")
    val urlToImage:String?=""

)