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

import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

fun isWithinSelectedRange(publishedDate: String, startMillis: Long?, endMillis: Long?): Boolean {
    if (startMillis == null && endMillis == null) return true

    return try {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val newsDateMillis = formatter.parse(publishedDate)?.time ?: return false

        val afterStart = startMillis?.let { newsDateMillis >= it } ?: true
        val beforeEnd = endMillis?.let { newsDateMillis <= it } ?: true

        afterStart && beforeEnd
    } catch (e: Exception) {
        false
    }
}



@Composable
fun NewsFeedScreen(
    navController: NavController,
    initialSearch: String = "",
    currentCategory: String = "Sve",
    currentStartDate: String? = null,
    currentEndDate: String? = null,
    currentUnwantedWords: List<String> = emptyList()
) {
    val allNewsItems = remember { NewsData.getAllNews() }

    var selectedCategoryLocal by remember(currentCategory) { mutableStateOf(currentCategory) }

    val startMillis = remember(currentStartDate) { parseDateToUTCEpochMillis(currentStartDate) }
    val endMillis = remember(currentEndDate) { parseDateToUTCEpochMillis(currentEndDate) }

    val unwantedLower = remember(currentUnwantedWords) {
        currentUnwantedWords.map { it.lowercase() }.toSet()
    }

    val filteredNews = remember(selectedCategoryLocal, startMillis, endMillis, unwantedLower, allNewsItems) {
        allNewsItems.filter { newsItem ->
            val categoryMatch = selectedCategoryLocal == "Sve" || newsItem.category == selectedCategoryLocal
            val dateMatch = isWithinSelectedRange(newsItem.publishedDate, startMillis, endMillis)
            val unwantedMatch = if (unwantedLower.isEmpty()) {
                true
            } else {
                val titleLower = newsItem.title.lowercase()
                !unwantedLower.any { unwantedWord -> titleLower.contains(unwantedWord) }
            }

            categoryMatch && dateMatch && unwantedMatch
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Sve", "Politika", "Sport").forEach { category ->
                val isSelected = selectedCategoryLocal == category
                FilterChip(
                    onClick = { if (!isSelected) selectedCategoryLocal = category },
                    label = { Text(category) },
                    selected = isSelected,
                    leadingIcon = if (isSelected) { { Icon(Icons.Filled.Done, "Done") } } else null,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Nauka/tehnologija", "Ostalo").forEach { category ->
                val isSelected = selectedCategoryLocal == category
                FilterChip(
                    onClick = { if (!isSelected) selectedCategoryLocal = category },
                    label = { Text(category, maxLines = 1) },
                    selected = isSelected,
                    leadingIcon = if (isSelected) { { Icon(Icons.Filled.Done, "Done") } } else null,
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

        FilterChip(
            onClick = {
                val encodedCategory = URLEncoder.encode(selectedCategoryLocal, StandardCharsets.UTF_8.toString())
                val encodedStartDate = URLEncoder.encode(currentStartDate ?: "", StandardCharsets.UTF_8.toString())
                val encodedEndDate = URLEncoder.encode(currentEndDate ?: "", StandardCharsets.UTF_8.toString())
                val encodedUnwanted = URLEncoder.encode(currentUnwantedWords.joinToString(","), StandardCharsets.UTF_8.toString())

                val route = "filters?" +
                        "category=$encodedCategory" +
                        "&startDate=$encodedStartDate" +
                        "&endDate=$encodedEndDate" +
                        "&unwanted=$encodedUnwanted"

                navController.navigate(route)
            },
            label = { Text("Više filtera ...") },
            modifier = Modifier.testTag("filter_chip_more"),
            selected = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji $selectedCategoryLocal")
        } else {
            NewsList(filteredNews, navController)
        }
    }
}






