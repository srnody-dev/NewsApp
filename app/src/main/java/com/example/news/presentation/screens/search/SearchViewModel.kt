package com.example.news.presentation.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.entity.Article
import com.example.news.domain.usecase.GetArticlesForQueryUseCase
import com.example.news.domain.usecase.GetTopArticlesUseCase
import com.example.news.domain.usecase.UpdateArticlesForQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val updateArticlesForQueryUseCase: UpdateArticlesForQueryUseCase,
    private val getArticlesForQueryUseCase: GetArticlesForQueryUseCase,
    private val getTopArticlesUseCase: GetTopArticlesUseCase

) : ViewModel() {

    private val query = MutableStateFlow("")
    private var _state = MutableStateFlow<SearchState>(SearchState.Search())

    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            query
                .flatMapLatest { input ->
                    _state.update { SearchState.Search(input) }
                    Log.d("SearchVM", "Processing query: '$input'")



                    if (input.isBlank()) {
                        Log.d("SearchVM", "Showing top articles")
                        getTopArticlesUseCase()
                            .map { articles ->
                                Log.d("SearchVM", "Top articles: ${articles.size}")
                                SearchState.Search(
                                    query = input,
                                    topArticles = articles,
                                )
                            }
                    } else {
                        Log.d("SearchVM", "Updating and searching")
                        launch {
                            try {
                                updateArticlesForQueryUseCase(input)
                            } catch (e: Exception) {
                                Log.e("SearchVM", "Background update failed", e)
                            }
                        }
                        getArticlesForQueryUseCase(input)
                            .map { articles ->
                                Log.d("SearchVM", "Search results: ${articles.size}")
                                SearchState.Search(
                                    query = input,
                                    resultArticles = articles
                                )
                            }
                    }
                }
                .collect { newState ->
                    Log.d("SearchVM", "New state: $newState")
                    _state.update { newState }
                }
        }
    }

    fun processCommand(command: SearchCommand) {
        when (command) {
            SearchCommand.Back -> {
                _state.update { SearchState.Finished }
            }

            is SearchCommand.InputQuery -> {
                query.update { command.query.trim() }
            }

        }
    }

    sealed interface SearchCommand {
        data object Back : SearchCommand
        data class InputQuery(val query: String) : SearchCommand
    }

    sealed interface SearchState {

        data class Search(
            val query: String = "",
            val topArticles: List<Article> = emptyList(),
            val resultArticles: List<Article> = emptyList()
        ) : SearchState

        data object Finished : SearchState
    }

}