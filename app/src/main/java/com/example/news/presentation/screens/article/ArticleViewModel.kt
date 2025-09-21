package com.example.news.presentation.screens.article

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.domain.usecase.GetArticleByTopicUseCase
import com.example.news.domain.usecase.GetTopArticlesUseCase
import com.example.news.domain.usecase.UpdateArticlesForAllTopicUseCase
import com.example.news.domain.usecase.UpdateArticlesForTopicUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val getTopArticlesUseCase: GetTopArticlesUseCase,
    private val updateArticlesForTopicUseCase: UpdateArticlesForTopicUseCase,
    private val updateArticlesForAllTopicUseCase: UpdateArticlesForAllTopicUseCase,
    private val getArticleByTopicUseCase: GetArticleByTopicUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ArticleScreenState())
    val state = _state.asStateFlow()

    private val selectedTopic = MutableStateFlow<Topic?>(Topic.GENERAL)

    init {
        getTopArticles()
        Log.d("ArticleViewModel", "getTopArticles() called successfully")

        selectedTopic.onEach { topic ->
            Log.d("ArticleViewModel", "Selected topic changed: $topic")
            _state.update {
                it.copy(selectedTopic = topic)
            }

            when {
                topic != null -> {
                    Log.d("ArticleViewModel", "Loading articles for topic: $topic")
                    loadArticlesForTopic(topic)
                }

                else -> {
                    Log.d("ArticleViewModel", "Loading articles for GENERAL topic")
                    loadArticlesForTopic(Topic.GENERAL)
                }
            }
        }.launchIn(viewModelScope)
        Log.d("ArticleViewModel", "Topic observer set up successfully")


        Log.d("ArticleViewModel", "ViewModel initialization completed")
    }

    fun onTopicSelected(topic: Topic) {
        Log.d("ArticleViewModel", "onTopicSelected: $topic")
        selectedTopic.value = if (selectedTopic.value == topic) {
            Log.d("ArticleViewModel", "Deselecting topic: $topic")
            null
        } else {
            Log.d("ArticleViewModel", "Selecting topic: $topic")
            topic
        }
    }

    private fun getTopArticles() {
        Log.d("ArticleViewModel", "getTopArticles() started")
        viewModelScope.launch {
            Log.d("ArticleViewModel", "Calling getTopArticlesUseCase()")
            val articles = getTopArticlesUseCase().first()
            Log.d("ArticleViewModel", "Received ${articles.size} top articles")
            _state.update { it.copy(topArticle = articles) }
            Log.d("ArticleViewModel", "State updated with top articles")


        }
    }

    private fun loadArticlesForTopic(topic: Topic) {
        Log.d("ArticleViewModel", "loadArticlesForTopic() started for: $topic")
        viewModelScope.launch {
            try {
                Log.d("ArticleViewModel", "Updating articles for topic: ${topic.name}")
                updateArticlesForTopicUseCase(topic.name)
                Log.d("ArticleViewModel", "Articles updated, now fetching from DB")

                val articles = getArticleByTopicUseCase(topic).first()
                Log.d("ArticleViewModel", "Received ${articles.size} articles for topic: $topic")

                _state.update { it.copy(topicArticle = articles) }
                Log.d("ArticleViewModel", "State updated with topic articles")

            } catch (e: Exception) {
                Log.e("ArticleViewModel", "loadArticlesForTopic ERROR for $topic: ${e.message}", e)
            }
        }
    }

    fun processCommand(command: ArticleCommand) {
        Log.d("ArticleViewModel", "processCommand: $command")
        viewModelScope.launch {
            try {
                when (command) {
                    ArticleCommand.RefreshArticles -> {
                        Log.d("ArticleViewModel", "Refreshing all articles")
                        updateArticlesForAllTopicUseCase()
                        Log.d("ArticleViewModel", "Refresh completed")
                    }
                }
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "processCommand ERROR: ${e.message}", e)
            }
        }
    }
}

sealed interface ArticleCommand {
    data object RefreshArticles : ArticleCommand
}

data class ArticleScreenState(
    val topArticle: List<Article> = emptyList(),
    val topicArticle: List<Article> = emptyList(),
    val selectedTopic: Topic? = null
) {
    val isTopicSelected: Boolean
        get() = selectedTopic != null
}
