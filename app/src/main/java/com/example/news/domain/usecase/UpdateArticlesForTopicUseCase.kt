package com.example.news.domain.usecase

import com.example.news.domain.entity.Article
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateArticlesForTopicUseCase @Inject constructor(private val newsRepository: NewsRepository) {
    suspend operator fun invoke(topic: String){
        return newsRepository.updateArticlesForTopic(topic)
    }
}