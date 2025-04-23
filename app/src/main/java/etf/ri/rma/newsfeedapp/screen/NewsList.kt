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
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsList(
    newsList: List<NewsItem>,
    navController: NavController,
    filterData: FilterData,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("news_list"),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(newsList) { news ->
            if (news.isFeatured)
                FeaturedNewsCard(
                    news = news,
                    navController = navController,
                    filterData = filterData
                )
            else
                StandardNewsCard(
                    news = news,
                    navController = navController,
                    filterData = filterData
                )
        }
    }
}
