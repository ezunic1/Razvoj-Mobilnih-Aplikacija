package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.data.network.ImageDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.R
import java.net.URLEncoder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsDetailsScreen(
    news: NewsItem,
    navController: NavController,
    filters: FilterData
) {
    val similarNewsState = produceState(initialValue = emptyList<NewsItem>(), news.uuid) {
        value = try {
            NewsDAO().getSimilarStories(news.uuid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    val imageTags by produceState(initialValue = emptyList<String>(), news.imageUrl) {
        value = try {
            if (!news.imageUrl.isNullOrBlank()) {
                ImageDAO().getTags(news.imageUrl)
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    val route = "home?" +
                            "category=${URLEncoder.encode(filters.category, "UTF-8")}" +
                            "&startDate=${URLEncoder.encode(filters.startDate ?: "", "UTF-8")}" +
                            "&endDate=${URLEncoder.encode(filters.endDate ?: "", "UTF-8")}" +
                            "&unwanted=${URLEncoder.encode(filters.unwantedWords.joinToString(","), "UTF-8")}"
                    navController.navigate(route) {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (!news.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(news.imageUrl)
                            .crossfade(true)
                            .error(R.drawable.knjiga)     // fallback slika
                            .placeholder(R.drawable.knjiga)
                            .build(),
                        contentDescription = "Slika vijesti",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("details_image")
                    )
                }
            }

            item {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("details_title")
                )
            }

            item {
                Text(
                    text = news.snippet,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("details_snippet")
                )
            }

            item {
                Text(
                    text = "Kategorija: ${news.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("details_category")
                )
            }

            item {
                Text(
                    text = "Izvor: ${news.source}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("details_source")
                )
            }

            item {
                Text(
                    text = "Datum objave: ${news.publishedDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("details_date")
                )
            }

            if (imageTags.isNotEmpty()) {
                item {
                    Text(
                        text = "Tagovi slike:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        imageTags.forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag) }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Povezane vijesti iz iste kategorije",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            itemsIndexed(similarNewsState.value) { index, item ->
                Text(
                    text = item.title,
                    modifier = Modifier
                        .testTag("related_news_title_${index + 1}")
                        .padding(vertical = 4.dp)
                        .clickable {
                            val route = "details/${item.uuid}?" +
                                    "category=${URLEncoder.encode(filters.category, "UTF-8")}" +
                                    "&startDate=${URLEncoder.encode(filters.startDate ?: "", "UTF-8")}" +
                                    "&endDate=${URLEncoder.encode(filters.endDate ?: "", "UTF-8")}" +
                                    "&unwanted=${URLEncoder.encode(filters.unwantedWords.joinToString(","), "UTF-8")}"
                            navController.navigate(route)
                        },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    BackHandler {
        val route = "home?" +
                "category=${URLEncoder.encode(filters.category, "UTF-8")}" +
                "&startDate=${URLEncoder.encode(filters.startDate ?: "", "UTF-8")}" +
                "&endDate=${URLEncoder.encode(filters.endDate ?: "", "UTF-8")}" +
                "&unwanted=${URLEncoder.encode(filters.unwantedWords.joinToString(","), "UTF-8")}"
        navController.navigate(route) {
            popUpTo("home") { inclusive = true }
        }
    }
}
