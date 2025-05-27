package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.R
import java.net.URLEncoder

@Composable
fun FeaturedNewsCard(
    news: NewsItem,
    navController: NavController,
    filterData: FilterData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate(
                    "details/${news.uuid}?category=${URLEncoder.encode(filterData.category, "UTF-8")}" +
                            "&startDate=${URLEncoder.encode(filterData.startDate ?: "", "UTF-8")}" +
                            "&endDate=${URLEncoder.encode(filterData.endDate ?: "", "UTF-8")}" +
                            "&unwanted=${URLEncoder.encode(filterData.unwantedWords.joinToString(","), "UTF-8")}"
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(news.imageUrl)
                    .crossfade(true)
                    .error(R.drawable.knjiga)     // placeholder ako URL ne radi
                    .placeholder(R.drawable.knjiga)
                    .build(),
                contentDescription = "Featured news image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.snippet,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${news.source} • ${news.publishedDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
