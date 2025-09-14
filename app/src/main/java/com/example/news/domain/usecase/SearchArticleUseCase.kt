package com.example.news.domain.usecase

import com.example.news.domain.entity.Article
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArticleUseCase @Inject constructor(private val newsRepository: NewsRepository) {


    suspend operator fun invoke(query: String): Flow<List<Article>>{
        return newsRepository.searchArticle(query)
    }

}