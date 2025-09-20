package com.example.news.data.repository

import android.util.Log
import com.example.news.data.local.ArticleDbModel
import com.example.news.data.local.NewsDao
import com.example.news.data.mapper.toDbModel
import com.example.news.data.mapper.toDbModelForSearch
import com.example.news.data.mapper.toEntities
import com.example.news.data.remote.NewsApiService
import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService

) : NewsRepository {

    override suspend fun updateArticlesForQuery(query: String) {
        try {
            Log.d("NewsRepository", "Updating articles for query: $query")
            val articles = loadArticlesForSearch(query)
            Log.d("NewsRepository", "Loaded ${articles.size} articles for $query")

            val insertedIds = newsDao.addArticles(articles)
            val successfulInserts = insertedIds.count { it != -1L }

            Log.d("NewsRepository", "Successfully inserted $successfulInserts articles for $query")

        } catch (e: Exception) {
            Log.e("NewsRepository", "Failed to update query: $query", e)
        }
    }
    override suspend fun updateArticlesForTopic(topic: String) {
        try {
            Log.d("NewsRepository", "Updating articles for topic: $topic")
            val articles = loadArticles(topic)
            Log.d("NewsRepository", "Loaded ${articles.size} articles for $topic")

            val insertedIds = newsDao.addArticles(articles)
            val successfulInserts = insertedIds.count { it != -1L }

            Log.d("NewsRepository", "Successfully inserted $successfulInserts articles for $topic")

        } catch (e: Exception) {
            Log.e("NewsRepository", "Failed to update topic: $topic", e)
        }
    }
    override suspend fun updateTopArticles() {
        try {

            Log.d("NewsRepository", "Making API calls for TopArticles:")
            val response = newsApiService.loadTopArticles()

            Log.d("NewsRepository", "API response for TopArticles ${response.articles.size} articles")
            val articles = response.toDbModel(topic = "") //????

            Log.d("NewsRepository", "Converted to ${articles.size} DB models")


            Log.d("NewsRepository", "Loaded ${articles.size} articles for TopArticles")

            //newsDao.addArticles(articles)
            val insertedIds = newsDao.addArticles(articles)
            val successfulInserts = insertedIds.count { it != -1L }

            Log.d("NewsRepository", "Successfully inserted $successfulInserts articles for TopArticles")
        } catch (e: Exception) {
            Log.e("NewsRepository", "Failed to update top articles", e)
        }
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            Log.d("NewsRepository", "Making API call for topic: $topic")
            val response = newsApiService.loadArticles(topic)
            Log.d("NewsRepository", "API response for $topic: ${response.articles.size} articles")

            val dbModels = response.toDbModel(topic)
            Log.d("NewsRepository", "Converted to ${dbModels.size} DB models")

            dbModels
        } catch (e: Exception) {
            Log.e("NewsRepository", "API Error for $topic: ${e.message}")
            listOf()
        }
    }


    override fun getArticlesForQuery(query: String): Flow<List<Article>> {
        return newsDao.searchArticle(query).map { it.toEntities() }
    }


    override fun getArticleByTopic(topic: Topic): Flow<List<Article>> {
        return newsDao.getArticlesByTopic(topic.name).map { it.toEntities() }
    }
    override  fun getTopArticles(): Flow<List<Article>> {
        return newsDao.getPopularArticles().map { it.toEntities() }
    }


    override suspend fun updateArticlesForAllTopic() {
        val topicsToUpdate = Topic.entries

        topicsToUpdate.forEach { topic ->
            try {
                updateArticlesForTopic(topic.name)
                updateTopArticles()
            } catch (e: Exception) {
                Log.e("NewsRepository", "Failed to update ${topic.name}", e)
            }
        }
    }

    private suspend fun loadArticlesForSearch(query: String): List<ArticleDbModel> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("NewsRepository", "Making API call for query: '$query'")
                val response = newsApiService.loadArticlesForQuery(query)

                Log.d("NewsRepository", "API response for '$query': ${response.articles.size} articles")

                val dbModels = response.toDbModelForSearch(query)
                Log.d("NewsRepository", "Converted to ${dbModels.size} DB models")

                dbModels

            } catch (e: CancellationException) {
                Log.d("NewsRepository", "Request cancelled for '$query'")
                emptyList()
            } catch (e: Exception) {
                Log.e("NewsRepository", "API Error for '$query': ${e.message}")
                emptyList()
            }
        }
    }

}