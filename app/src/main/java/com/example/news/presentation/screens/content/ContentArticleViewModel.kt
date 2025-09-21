package com.example.news.presentation.screens.content

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.entity.Article
import com.example.news.domain.usecase.GetArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import android.net.Uri

@HiltViewModel()
class ContentArticleViewModel @Inject constructor(
    private val getArticleUseCase: GetArticleUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<ContentArticleState>(ContentArticleState.Initial)

    val state = _state.asStateFlow()


    init {
        val url = savedStateHandle.get<String>("url") ?: ""
        Log.d("ContentArticleViewModel", "Received URL: $url")
        if (url.isNotBlank()) loadArticle(url)
    }


    fun loadArticle(url: String) {
        _state.value = ContentArticleState.Initial
        getArticleUseCase(url = url)
            .onEach { article ->
                _state.value = ContentArticleState.Content(article = article)
            }
            .catch { error ->
                Log.e("ContentArticleViewModel", "LoadArticles error: ${error.message}", error)
            }
            .launchIn(viewModelScope)
    }


    fun processCommand(command: ContentArticleCommand) {
        when (command) {
            ContentArticleCommand.Back -> {
                _state.update { ContentArticleState.Finished }
            }

            is ContentArticleCommand.SwitchPinnedStatus -> {
                //РЕАЛИЗУЙ ПОЗЖЕ
            }
        }
/*
        @AssistedFactory
        interface Factory{
            fun create(@Assisted("url")url: String): ContentArticleViewModel
        }*/
    }


    sealed interface ContentArticleCommand {
        data object Back : ContentArticleCommand
        data class SwitchPinnedStatus(val articleId: Int) : ContentArticleCommand
    }

    sealed interface ContentArticleState {
        data object Finished : ContentArticleState
        data object Initial : ContentArticleState


        data class Content(
            val article: Article
        ) : ContentArticleState
    }
}