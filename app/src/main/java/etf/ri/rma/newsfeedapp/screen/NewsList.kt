package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsList( newsList: List<NewsItem>,
              navController: NavController,
              currentCategory: String,
              currentStartDate: String?,
              currentEndDate: String?,
              currentUnwantedWords: List<String>,
              modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("news_list"),
        // contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(newsList) { news ->
            if(news.isFeatured)
                FeaturedNewsCard(
                    news,
                    navController,
                    currentCategory,
                    currentStartDate,
                    currentEndDate,
                    currentUnwantedWords
                )
            else
                StandardNewsCard(
                    news,
                    navController,
                    currentCategory,
                    currentStartDate,
                    currentEndDate,
                    currentUnwantedWords
                )
        }
    }
}


