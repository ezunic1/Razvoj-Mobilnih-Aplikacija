package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.R
import java.net.URLEncoder

@Composable
fun StandardNewsCard(
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
        Row(modifier = Modifier.padding(16.dp)) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(news.imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.knjiga)
                    .error(R.drawable.knjiga)
                    .build(),
                contentDescription = "News image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(5.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = news.snippet,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${news.source} • ${news.publishedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
