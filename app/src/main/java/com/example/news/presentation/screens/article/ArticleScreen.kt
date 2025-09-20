package com.example.news.presentation.screens.article

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.entity.Article
import com.example.news.domain.entity.Topic
import com.example.news.presentation.utils.formatDate

@Composable
fun ArticleScreen(
    modifier: Modifier = Modifier,
    onNavigateToMenu: () -> Unit,
    onAllTrendingNews: () -> Unit,
    onClickToSearch: () -> Unit,
    onArticleClick: (Article) -> Unit,
    viewModel: ArticleViewModel = viewModel()
) {
    val state = viewModel.state.collectAsState()


    Scaffold(modifier = modifier.fillMaxSize(), topBar = {

        ArticleTopBar(modifier= Modifier,
            onClickToMenu = onNavigateToMenu,
            onClickToSearch = onClickToSearch)

    }) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {

            TrendingNewsTitle(onAllTrendingNews = onAllTrendingNews)

            LazyRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(state.value.topArticle, key = { it.url }) { article ->
                    ArticleCard(
                        modifier = Modifier.widthIn(max = 305.dp, min = 120.dp),
                        article = article, onArticleClick = onArticleClick
                    )
                }

            }
            LazyRow(
                modifier = Modifier,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Topic.entries) { topic ->
                    TopicChip(
                        isSelected = state.value.selectedTopic == topic, // Проверяем выбран ли этот topic
                        topic = topic,
                        onTopicClick = { viewModel.onTopicSelected(it) }
                    )
                }
            }
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                items(state.value.topicArticle, key = { it.url }) { article ->
                    ArticleCard(article = article, onArticleClick = onArticleClick)
                }
            }
        }
    }

}

@Composable
private fun TopicChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    topic: Topic,
    onTopicClick: (Topic) -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = { onTopicClick(topic) },
        label = {
            Text(
                text = topic.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurface
            )
                },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleTopBar(
    modifier: Modifier = Modifier,
    onClickToMenu: () -> Unit,
    onClickToSearch: () -> Unit
) {
    TopAppBar(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        title = {
            Text(
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                text = stringResource(R.string.article_title),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClickToMenu)
                    .padding(8.dp)
            )
        },
        actions = {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClickToSearch)
                    .padding(8.dp)
            )
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClickToMenu)
                    .padding(8.dp)

            )
        }
    )
}

@Composable
private fun TrendingNewsTitle(
    modifier: Modifier = Modifier,
    onAllTrendingNews: () -> Unit
) {
    Row(modifier = modifier.padding(18.dp)) {
        Text(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            text = stringResource(R.string.trending_news),
            modifier = Modifier.weight(1f),
        )
        Text(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = stringResource(R.string.see_all),
            modifier = Modifier.clickable(onClick = onAllTrendingNews)
        )
    }
}


@Composable
private fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article,
    onArticleClick: (Article) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = { onArticleClick(article) })
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(8.dp)

    ) {

        article.imageUrl?.let { imageUrl ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 161.dp)
                    .widthIn(max = 285.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )

        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
            text = article.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f),
                text = article.sourceName ?: "Неизвестный Источник"
            )
            Text(
                text = article.publishedAt.formatDate(),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )

        }

    }

}
