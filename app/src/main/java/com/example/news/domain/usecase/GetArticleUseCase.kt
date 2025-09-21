package com.example.news.domain.usecase

import com.example.news.domain.entity.Article
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(url: String): Flow<Article>{
        return newsRepository.getArticle(url)
    }
}