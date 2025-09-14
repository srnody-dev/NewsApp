package com.example.news.domain.usecase

import com.example.news.domain.entity.Article
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticleByTopicUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(topic: String): Flow<List<Article>>{
        return newsRepository.getArticleByTopic(topic)
    }
}