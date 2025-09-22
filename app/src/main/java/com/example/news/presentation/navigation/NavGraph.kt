package com.example.news.presentation.navigation

import androidx.navigation.navArgument
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.news.presentation.screens.article.ArticleScreen
import com.example.news.presentation.screens.content.ContentArticle
import com.example.news.presentation.screens.search.SearchScreen
import android.net.Uri
import com.example.news.presentation.screens.trend.TrendingArticles

@Composable
fun NavGraph(

) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Article.route
    ) {
        composable(Screen.Article.route) {
            ArticleScreen(
                onNavigateToMenu = {},
                onAllTrendingNews = { navController.navigate(Screen.TrendingArticles.route) },
                onClickToSearch = { navController.navigate(Screen.Search.route) },
                onArticleClick = { article ->
                    navController.navigate(Screen.ContentArticle.createRoute(article.url))
                }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onFinished = {
                    navController.popBackStack()
                }, onArticleClick = { article ->
                    navController.navigate(Screen.ContentArticle.createRoute(article.url))
                }
            )
        }
        composable(
            route = Screen.ContentArticle.route,
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            ContentArticle(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.TrendingArticles.route) {
            TrendingArticles(
                onFinish = {
                    navController.popBackStack()
                }, onArticleClick = { article ->
                    navController.navigate(Screen.ContentArticle.createRoute(article.url))
                }
            )
        }

    }
}


sealed class Screen(val route: String) {
    data object Article : Screen("article")
    data object Search : Screen("search")
    data object ContentArticle : Screen("contentArticle?url={url}") {
        fun createRoute(url: String): String {
            val encodedUrl = Uri.encode(url)
            return "contentArticle?url=$encodedUrl"
        }
    }

    data object TrendingArticles : Screen("trend")
}