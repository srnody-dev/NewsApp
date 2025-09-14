package com.example.news.data.repository

import android.util.Log
import com.example.news.data.local.ArticleDbModel
import com.example.news.data.local.NewsDao
import com.example.news.data.mapper.toDbModel
import com.example.news.data.mapper.toEntities
import com.example.news.data.remote.NewsApiService
import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject
class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService

) : NewsRepository {
    override suspend fun searchArticle(query: String): Flow<List<Article>> {
        return newsDao.searchArticle(query).map { it.toEntities() }
    }


    override fun getTopArticles(): Flow<List<Article>> {
        TODO()
    }



    override suspend fun updateArticlesForTopic(topic: String) {
        try {
            val articles = loadArticles(topic)
            newsDao.addArticles(articles)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Failed to update topic: $topic", e)
        }
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            newsApiService.loadArticles(topic).toDbModel(topic)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            listOf()
        }
    }

    override fun getArticleByTopic(topic: Topic): Flow<List<Article>> {
        return newsDao.getArticlesByTopic(topic.name).map { it.toEntities() }
    }

    override suspend fun updateTopArticles() {
        try {
            val response = newsApiService.getTopArticles(sortBy = "popularity")
            val articles = response.toDbModel("popularity") // Сохраняем с topic = "top"
            newsDao.addArticles(articles)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Failed to update top articles", e)
        }
    }

    override suspend fun updateArticlesForAllTopic() {
        val topicsToUpdate = Topic.entries

        topicsToUpdate.forEach { topic ->
            try {
                updateArticlesForTopic(topic.name) // Обновляем каждую тему
                updateTopArticles()
            } catch (e: Exception) {
                Log.e("NewsRepository", "Failed to update ${topic.name}", e)
            }
        }

        updateTopArticles()
    }



    override suspend fun removeArticlesForTopic(topic: String) {
        newsDao.deleteArticlesByTopics(topic)
    }

}