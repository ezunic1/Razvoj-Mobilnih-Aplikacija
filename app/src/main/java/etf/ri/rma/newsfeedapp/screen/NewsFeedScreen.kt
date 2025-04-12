package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.R

@Composable
fun NewsFeedScreen(newsItems: List<NewsItem>) {
    val allCategories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Ostalo")

    var selectedCategory by remember { mutableStateOf("Sve") }

    val filteredNews = if (selectedCategory == "Sve") {
        newsItems
    } else {
        newsItems.filter { it.category == selectedCategory }
    }

    Column(modifier = Modifier.fillMaxSize().padding(5.dp)) {
        Spacer(modifier = Modifier.height(30.dp))


        LazyColumn {
            item {
                // Prvi red filtera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Sve", "Politika", "Sport").forEach { category ->
                        val selected = selectedCategory == category
                        FilterChip(
                            onClick = {
                                if (!selected) selectedCategory = category
                            },
                            label = {
                                Text(category)
                            },
                            selected = selected,
                            leadingIcon = if (selected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
                            modifier = Modifier.testTag(
                                when (category) {
                                    "Sve" -> "filter_chip_all"
                                    "Politika" -> "filter_chip_pol"
                                    "Sport" -> "filter_chip_spo"
                                    else -> ""
                                }
                            ).weight(1f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Nauka/tehnologija", "Ostalo").forEach { category ->
                        val selected = selectedCategory == category
                        FilterChip(
                            onClick = {
                                if (!selected) selectedCategory = category
                            },
                            label = {
                                Text(category)
                            },
                            selected = selected,
                            leadingIcon = if (selected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
                            modifier = Modifier.testTag(
                                when (category) {
                                    "Nauka/tehnologija" -> "filter_chip_sci"
                                    "Ostalo" -> "filter_chip_none"
                                    else -> ""
                                }
                            ).weight(1f)
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji " + selectedCategory)
        } else {
            NewsList(filteredNews)
        }
    }
}








