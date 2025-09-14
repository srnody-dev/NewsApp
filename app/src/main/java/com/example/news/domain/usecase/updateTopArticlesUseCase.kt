package com.example.news.domain.usecase

import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateTopArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke() {
        newsRepository.updateTopArticles()
    }
}