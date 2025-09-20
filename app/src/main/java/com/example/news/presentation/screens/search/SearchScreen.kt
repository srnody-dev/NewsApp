package com.example.news.presentation.screens.search

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onFinished: () -> Unit,
    onArticleClick: (Article) -> Unit
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value


    when (currentState) {
        SearchViewModel.SearchState.Finished -> {
            onFinished()
        }

        is SearchViewModel.SearchState.Initial -> {
            Scaffold(modifier = modifier) { innerPadding ->
                Column(modifier = modifier.padding(8.dp)) {


                Row(modifier = modifier.fillMaxWidth()
                    .padding(innerPadding),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .size(35.dp)
                            .clickable { viewModel.processCommand(SearchViewModel.SearchCommand.Back) }
                        ,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                    SearchBar(
                        modifier=modifier.padding(start = 8.dp, end = 8.dp),
                        query = currentState.query,
                        onQueryChange = { query ->
                            viewModel.processCommand(SearchViewModel.SearchCommand.InputQuery(query))
                        }
                    )
                }

                    val title:String = if (currentState.query.isBlank()) {
                        "Popular News"
                    } else "Найдено ${currentState.resultArticles.size}"

                    Text(modifier= Modifier.padding(16.dp),
                        text=title
                    )

                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    val articles = if (currentState.query.isBlank()) {
                        currentState.topArticles
                    } else currentState.resultArticles
                    items(articles, key = { it.url }) { article ->

                        ArticleCard(article = article, onArticleClick = onArticleClick)

                    }
                }
            }

            }

        }
    }

}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_news),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
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
