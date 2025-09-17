package com.example.news.presentation.screens.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.domain.usecase.GetArticleByTopicUseCase
import com.example.news.domain.usecase.GetTopArticlesUseCase
import com.example.news.domain.usecase.SearchArticleUseCase
import com.example.news.domain.usecase.UpdateArticlesForAllTopicUseCase
import com.example.news.domain.usecase.UpdateArticlesForTopicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List




@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val searchArticleUseCase: SearchArticleUseCase,
    private val getTopArticlesUseCase: GetTopArticlesUseCase,
    private val updateArticlesForTopicUseCase: UpdateArticlesForTopicUseCase,
    private val updateArticlesForAllTopicUseCase: UpdateArticlesForAllTopicUseCase,
    private val getArticleByTopicUseCase: GetArticleByTopicUseCase
) : ViewModel() {


    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(ArticleScreenState())
    val state = _state.asStateFlow()

    private val selectedTopic = MutableStateFlow<Topic?>(Topic.GENERAL)

    init {
        getTopArticles()

        combine(query, selectedTopic) { searchQuery, topic ->
            Pair(searchQuery, topic)
        }.onEach { (searchQuery, topic) ->
            _state.update {
                it.copy(
                    query = searchQuery,
                    selectedTopic = topic
                )
            }

            when {
                searchQuery.isNotBlank() -> searchArticles(searchQuery)
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
                _state.update { it.copy(error = "getTopArticles failed: ${e.message}") }
            }
        }
    }

    private fun loadArticlesForTopic(topic: Topic) {
        viewModelScope.launch {
            try {
                // Обновляем статьи для темы
                updateArticlesForTopicUseCase(topic.name)

                // Получаем статьи для отображения
                val articles = getArticleByTopicUseCase(topic).first()
                _state.update { it.copy(topicArticle = articles) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "loadArticlesForTopic failed: ${e.message}") }
            }
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            try {
                val result = searchArticleUseCase(query).first()
                _state.update {  it.copy(topicArticle = result) } //ВОТ ЗДЕСЬ ПЕРЕПИШИ ТЫ ИЩЕШЬ ТОЛЬКО В НОВОСТЯХ КОТОРЫЕ УЖЕ ЕСТЬ
            } catch (e: Exception) {
                _state.update { it.copy(error = "Search failed: ${e.message}") }
            }
        }
    }

    fun processCommand(command: ArticleCommand) {
        viewModelScope.launch {
            when (command) {
                is ArticleCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }
                ArticleCommand.RefreshArticles -> {
                    updateArticlesForAllTopicUseCase()
                    // после обновления перезагружаем текущие данные
                    //selectedTopic.value?.let { loadArticlesForTopic(it) } ?: getTopArticles()
                }
            }
        }
    }
}
sealed interface ArticleCommand {

    data class InputSearchQuery(val query: String) : ArticleCommand

    data object RefreshArticles : ArticleCommand

}

data class ArticleScreenState(
    val query: String = "",
    val topArticle: List<Article> = emptyList(),
    val topicArticle: List<Article> = emptyList(),
    val selectedTopic: Topic? = null, // Только одна выбранная тема
    val loading: Boolean = false,
    val error: String? = null
) {
    val isTopicSelected: Boolean
        get() = selectedTopic != null
}