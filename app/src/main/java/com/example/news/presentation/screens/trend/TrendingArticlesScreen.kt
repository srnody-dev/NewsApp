package com.example.news.presentation.screens.trend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.entity.Article
import com.example.news.presentation.utils.formatDate


@Composable
fun TrendingArticles(
    modifier: Modifier = Modifier,
    viewmodel: TrendingArticlesViewModel = hiltViewModel(),
    onFinish: () -> Unit,
    onArticleClick: (Article) -> Unit
) {

    val state = viewmodel.state.collectAsState()
    val currentState = state.value


    when (currentState) {
        TrendingArticlesState.Finished -> {
            onFinish()
        }

        is TrendingArticlesState.TrendArticles -> {

            Scaffold(modifier = modifier) { innerPadding ->
                Column(modifier = modifier.padding(8.dp)) {


                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(35.dp)
                                .clickable { viewmodel.proccessCommand(TrendingArticlesCommand.Back) },
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }


                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                        text = "Trending Articles"
                    )

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {

                        items(currentState.topArticles, key = { it.url }) { article ->

                            ArticleCard(article = article, onArticleClick = onArticleClick)

                        }
                    }
                }

            }


        }
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
                text = article.sourceName ?: stringResource(R.string.unknown_source)
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
