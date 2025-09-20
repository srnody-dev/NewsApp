package com.example.news.domain.usecase

import com.example.news.domain.repository.NewsRepository
import javax.inject.Inject

class UpdateArticlesForQueryUseCase @Inject constructor(private val newsRepository: NewsRepository) {
    suspend operator fun invoke(query: String){
        return newsRepository.updateArticlesForQuery(query)
    }
}