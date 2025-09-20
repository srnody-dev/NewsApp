package com.example.news.presentation.screens.article

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List




@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val getTopArticlesUseCase: GetTopArticlesUseCase,
    private val updateArticlesForTopicUseCase: UpdateArticlesForTopicUseCase,
    private val updateArticlesForAllTopicUseCase: UpdateArticlesForAllTopicUseCase,
    private val getArticleByTopicUseCase: GetArticleByTopicUseCase
) : ViewModel(){


    private val _state = MutableStateFlow(ArticleScreenState())
    val state = _state.asStateFlow()

    private val selectedTopic = MutableStateFlow<Topic?>(Topic.GENERAL)

    init {
        getTopArticles()

        selectedTopic.onEach {  topic ->
            _state.update {
                it.copy(
                    selectedTopic = topic
                )
            }

            when {
                topic != null -> loadArticlesForTopic(topic) // загрузка для выбранной темы
                else -> loadArticlesForTopic(Topic.GENERAL) // если ни одна тема не выбрана то загрузка All
            }
        }.launchIn(viewModelScope)
    }

    fun onTopicSelected(topic: Topic) {
        selectedTopic.value = if (selectedTopic.value == topic) {
            null
        } else {
            topic
        }
    }

    private fun getTopArticles() {
        viewModelScope.launch {
            try {
                val articles = getTopArticlesUseCase().first()
                _state.update { it.copy(topArticle = articles) }
            } catch (e: Exception) {

            }
        }
    }

    private fun loadArticlesForTopic(topic: Topic) {
        viewModelScope.launch {
            try {
                updateArticlesForTopicUseCase(topic.name)
                val articles = getArticleByTopicUseCase(topic).first()
                _state.update { it.copy(topicArticle = articles)
                }

            } catch (e: Exception) {

            }
        }
    }

    fun processCommand(command: ArticleCommand) {
        viewModelScope.launch {
            when (command) {
                ArticleCommand.RefreshArticles -> {
                    updateArticlesForAllTopicUseCase()

                }
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