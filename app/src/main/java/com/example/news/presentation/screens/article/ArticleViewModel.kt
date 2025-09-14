package com.example.news.presentation.screens.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.domain.usecase.GetArticleByTopicUseCase
import com.example.news.domain.usecase.GetTopArticlesUseCase
import com.example.news.domain.usecase.SearchArticleUseCase
import com.example.news.domain.usecase.UpdateArticlesForAllTopicUseCase
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
    private val updateArticlesForAllTopicUseCase: UpdateArticlesForAllTopicUseCase,
    private val getArticleByTopicUseCase: GetArticleByTopicUseCase
) : ViewModel() {


    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(ArticleScreenState())
    val state = _state.asStateFlow()

    private val selectedTopics = MutableStateFlow<Set<Topic>>(emptySet())

    init {

        getTopArticles()
        combine(query, selectedTopics) { searchQuery, topics ->
            Pair(searchQuery, topics)
        }.onEach { (searchQuery, topic) ->

            _state.update { it.copy(query = searchQuery, topic = topic.associateWith { true }) }

            when {
                searchQuery.isNotBlank() -> searchArticles(query.toString())
                topic.isNotEmpty() -> loadArticlesForTopic(topic)
                else -> getAllArticles()
            }
        }.launchIn(viewModelScope)

    }

    fun processCommand(command: ArticleCommand) {
        viewModelScope.launch {
            when (command) {
                is ArticleCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                ArticleCommand.RefreshArticles -> {
                    updateArticlesForAllTopicUseCase()
                }
            }
        }

    }

    fun getTopArticles() {
        viewModelScope.launch {
            val articles = getTopArticlesUseCase().first()
            _state.update { it.copy(topicArticle = articles) }
        }
    }

    fun getAllArticles() {
        viewModelScope.launch {
            val articles = getArticleByTopicUseCase(Topic.ALL).first()
            _state.update { it.copy(topicArticle = articles) }
        }

    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            val result = searchArticleUseCase(query)
            _state.update { it.copy(topicArticle = result as List<Article>) }
        }
    }

    fun toggleTopic(topic: Topic) {
        selectedTopics.value = if (selectedTopics.value.contains(topic)) {
            emptySet()
        } else
            setOf(topic)

    }


    fun loadArticlesForTopic(topics: Set<Topic>) {
        viewModelScope.launch {

            val allArticles = topics.flatMap { topic ->
                getArticleByTopicUseCase(topic).first()
            }
            _state.update { it.copy(topicArticle = allArticles) }
        }
    }
}


sealed interface ArticleCommand {

    data class InputSearchQuery(val query: String) : ArticleCommand

    data object RefreshArticles : ArticleCommand

}

data class ArticleScreenState(
    val query: String = "",
    val topArticle: List<Article> = listOf(),
    val topic: Map<Topic, Boolean> = mapOf(),//список topic
    val topicArticle: List<Article> = listOf(),
    val selectedTopic: Topic? = null

) {
    val isTopicSelected: Boolean
        get() = selectedTopic != null

}