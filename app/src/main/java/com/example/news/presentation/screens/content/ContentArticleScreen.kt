package com.example.news.presentation.screens.content

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.room.util.TableInfo
import coil3.compose.AsyncImage
import com.example.news.presentation.utils.formatDate


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ContentArticle(
    onFinished: () -> Unit,
    viewModel: ContentArticleViewModel = hiltViewModel(

    )
) {
    val state = viewModel.state.collectAsState()
    var currentState = state.value


    when (currentState) {
        is ContentArticleViewModel.ContentArticleState.Content -> {


            Column(modifier = Modifier.fillMaxWidth()) {

                Box{
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                        ,
                        modifier = Modifier

                            .padding(top = 40.dp, start = 24.dp)
                            .align(Alignment.TopStart)
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            ).clickable { onFinished() }
                            .zIndex(1f)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                        ,
                        modifier = Modifier

                            .padding(top = 40.dp, end = 24.dp)
                            .align(Alignment.TopEnd)
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            ).clickable { onFinished() }
                            .zIndex(1f)
                    ) {
                        Icon(
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
                        fontWeight = FontWeight.SemiBold, letterSpacing= 1.sp,lineHeight = 30.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                    Row{
                        Text(modifier = Modifier.weight(1f),
                            text = currentState.article.sourceName ?: "Неизвестный источник",
                             fontSize = 16.sp,
                            fontWeight = FontWeight.Normal, letterSpacing= 1.sp,lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = currentState.article.publishedAt.formatDate(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal, letterSpacing= 1.sp,lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = currentState.article.content.dropLast(15) + "...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing= 1.sp,lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = {
                        }
                    ) { Text(
                        text = "Read full news",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing= 1.sp,lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )}
                }


            }

        }

        ContentArticleViewModel.ContentArticleState.Finished -> {
            onFinished()
        }

        ContentArticleViewModel.ContentArticleState.Initial -> {
            Text("INITIAL")
        }
    }


}


@Composable
private fun ImageArticle(imageUrl: String?) {

    imageUrl?.let { imageUrl ->
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 316.dp)
                    ,
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

    }
}