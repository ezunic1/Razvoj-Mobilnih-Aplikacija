package etf.ri.rma.newsfeedapp.screen

import android.util.Log
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
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.data.network.ImageDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.R
import etf.ri.rma.newsfeedapp.network.ImageAPI
import etf.ri.rma.newsfeedapp.network.NewsAPI
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsDetailsScreen(
    news: NewsItem, // Primljena vijest za prikaz
    navController: NavController, // Navigacija
    filters: FilterData, // Filteri koji su bili aktivni
    newsDAO: NewsDAO // DAO za dohvat sličnih vijesti
) {
    val similarNewsState = remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var loadingNext by remember { mutableStateOf(false) }

    // Dohvati slične vijesti kada se promijeni UUID
    LaunchedEffect(news.uuid) {
        try {
            similarNewsState.value = newsDAO.getSimilarStories(news.uuid)
        } catch (e: Exception) {
            similarNewsState.value = emptyList()
        }
    }

    // Dohvati tagove slike (ako postoji URL slike)
    val imageTags by produceState(initialValue = emptyList<String>(), news.imageUrl) {
        value = try {
            if (!news.imageUrl.isNullOrBlank()) {
                ImageDAO().apply { setApiService(ImageAPI.service) }.getTags(news.imageUrl)
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
                    // Vrati se nazad na početni ekran s istim filterima
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

            Spacer(modifier = Modifier.height(80.dp))



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
            // Slika vijesti (ako postoji)
            item {
                if (!news.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(news.imageUrl)
                            .crossfade(true)
                            .error(R.drawable.knjiga)
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

            // Naslov, opis, izvor i datum objave
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

            // Prikaz tagova slike ako ih ima
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

            // Slične vijesti iz iste kategorije
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
                        .clickable {
                            val route = "details/${item.uuid}?" +
                                    "category=${URLEncoder.encode(filters.category, "UTF-8")}" +
                                    "&startDate=${URLEncoder.encode(filters.startDate ?: "", "UTF-8")}" +
                                    "&endDate=${URLEncoder.encode(filters.endDate ?: "", "UTF-8")}" +
                                    "&unwanted=${URLEncoder.encode(filters.unwantedWords.joinToString(","), "UTF-8")}"
                            navController.navigate(route)
                        }
                        .padding(vertical = 4.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Donji razmak ispod zadnjeg elementa
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
            item{
                Button(
                    onClick = {
                        loadingNext = true
                        coroutineScope.launch {
                            val url = newsDAO.getSimilarWithSource(news.uuid, news.source)
                            loadingNext = false
                            url?.let {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, it.toUri())
                                context.startActivity(intent)
                            }
                        }
                    },
                    enabled = !loadingNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("details_next_button")
                ) {
                    Text(if (loadingNext) "Učitavanje..." else "Otvori sljedeću")
                }
            }




            /*val headlinesBySource = remember { mutableStateOf<List<String>>(emptyList()) }

            LaunchedEffect(news.source) {
                try {
                    headlinesBySource.value = newsDAO.getHeadlinesBySource(news.source)
                } catch (e: Exception) {
                    headlinesBySource.value = emptyList()
                }
            }*/

            //
            /*item {
                Text(
                    text = "Vijesti iz istog izvora: ${news.source}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            itemsIndexed(headlinesBySource.value) { index, title ->
                Text(
                    text = title,
                    modifier = Modifier.padding(vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }*/
            //

        }
    }

    // Hardverski back handler - identično ponašanje kao na "zatvori"
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