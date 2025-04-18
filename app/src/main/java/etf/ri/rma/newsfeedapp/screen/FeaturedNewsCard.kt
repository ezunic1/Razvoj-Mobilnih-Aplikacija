package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.R


@Composable
fun FeaturedNewsCard(news: NewsItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("details/${news.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.knjiga),
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