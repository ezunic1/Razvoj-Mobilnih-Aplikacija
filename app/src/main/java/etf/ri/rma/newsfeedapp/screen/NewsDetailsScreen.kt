package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


@Composable
fun NewsDetailsScreen(news: NewsItem, navController: NavController, initialSearch: String = "") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = news.title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("details_title")
        )

        Text(
            text = news.snippet,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("details_snippet")
        )

        Text(
            text = "Kategorija: ${news.category}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("details_category")
        )

        Text(
            text = "Izvor: ${news.source}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("details_source")
        )

        Text(
            text = "Datum objave: ${news.publishedDate}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("details_date")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Povezane vijesti iz iste kategorije",
            style = MaterialTheme.typography.headlineSmall
        )

        val relatedNews = getRelatedNews(news)
        relatedNews.forEachIndexed { index, item ->
            Text(
                text = item.title,
                modifier = Modifier
                    .testTag("related_news_title_${index + 1}")
                    .padding(vertical = 4.dp)
                    .clickable {
                        navController.navigate("details/${item.id}")
                    },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Zatvori detalje",
            modifier = Modifier
                .fillMaxWidth()
                .testTag("details_close_button")
                .clickable {
                    navController.popBackStack()
                },
            style = MaterialTheme.typography.labelLarge
        )
    }
}


fun getRelatedNews(currentNews: NewsItem): List<NewsItem> {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")

    val currentDate = formatter.parse(currentNews.publishedDate)?.time ?: return emptyList()

    return NewsData.getAllNews()
        .filter { it.category == currentNews.category && it.id != currentNews.id }
        .mapNotNull { news ->
            val newsDate = formatter.parse(news.publishedDate)?.time ?: return@mapNotNull null
            val timeDiff = kotlin.math.abs(newsDate - currentDate)
            Triple(news, timeDiff, news.title)
        }
        .sortedWith(compareBy({ it.second }, { it.third }))
        .map { it.first }
        .take(2)
}

