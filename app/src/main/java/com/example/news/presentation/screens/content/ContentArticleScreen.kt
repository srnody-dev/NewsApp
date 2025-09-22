package com.example.news.presentation.screens.content

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.news.R
import com.example.news.presentation.utils.formatDate

@Composable
fun ContentArticle(
    onFinished: () -> Unit,
    viewModel: ContentArticleViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    var currentState = state.value

    val context = LocalContext.current


    when (currentState) {
        is ContentArticleViewModel.ContentArticleState.Content -> {


            Column(modifier = Modifier.fillMaxWidth()) {

                Box {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier

                            .padding(top = 40.dp, start = 24.dp)
                            .align(Alignment.TopStart)
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onFinished() }
                            .zIndex(1f)
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier

                            .padding(top = 40.dp, end = 24.dp)
                            .align(Alignment.TopEnd)
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${currentState.article.title}\n\n${currentState.article.url}"
                                    )
                                }
                                context.startActivity(intent)
                            }
                            .zIndex(1f)
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    ImageArticle(imageUrl = currentState.article.imageUrl)
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {

                    Text(
                        text = currentState.article.title, fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, lineHeight = 30.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                    Row {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = currentState.article.sourceName ?: "Unknown source",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = currentState.article.publishedAt.formatDate(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = currentState.article.content.dropLast(15) + "...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp, lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.padding(16.dp))

                    Button(
                        onClick = {
                            val intent =
                                Intent(Intent.ACTION_VIEW, currentState.article.url.toUri())
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.read_full_article),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.sp, lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }


            }

        }

        ContentArticleViewModel.ContentArticleState.Finished -> {
            onFinished()
        }

        ContentArticleViewModel.ContentArticleState.Initial -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }


}


@Composable
private fun ImageArticle(imageUrl: String?) {

    imageUrl?.let { imageUrl ->
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 316.dp),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

    }
}