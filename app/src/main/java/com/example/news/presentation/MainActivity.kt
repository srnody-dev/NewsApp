package com.example.news.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.news.presentation.screens.article.ArticleScreen
import com.example.news.presentation.screens.search.SearchScreen
import com.example.news.presentation.theme.NewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsTheme {

                ArticleScreen(
                    onNavigateToMenu = {},
                    onAllTrendingNews = {},
                    onArticleClick = {},
                    onClickToSearch = {}
                )

                /*
                SearchScreen(
                    onFinished = {},
                    onArticleClick = {}
                )*/
            }

        }
    }
}