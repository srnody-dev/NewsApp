package com.example.news.presentation.screens.trend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.entity.Article
import com.example.news.domain.usecase.GetTopArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TrendingArticlesViewModel @Inject constructor(
    private val getTopArticlesUseCase: GetTopArticlesUseCase,

):  ViewModel() {


    private var _state= MutableStateFlow<TrendingArticlesState>(TrendingArticlesState.TrendArticles())
    val state=_state.asStateFlow()

    init {
        viewModelScope.launch {

                getTopArticlesUseCase().map { articles ->
                    TrendingArticlesState.TrendArticles(
                        topArticles = articles
                    )
                }.collect { newState ->
                    _state.update { newState }
                }


        }
    }



    fun proccessCommand(command: TrendingArticlesCommand){
        when(command){
            TrendingArticlesCommand.Back -> {
                _state.update { TrendingArticlesState.Finished }
            }
        }
    }






}


sealed interface TrendingArticlesCommand{

    data object Back: TrendingArticlesCommand
}

sealed interface TrendingArticlesState{
    data class TrendArticles(val topArticles: List<Article> = emptyList()): TrendingArticlesState
    data object Finished: TrendingArticlesState
}