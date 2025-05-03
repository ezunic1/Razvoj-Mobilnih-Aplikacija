package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.data.NewsData
import java.net.URLEncoder
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
    filterData: FilterData
) {
    val allNewsItems = remember { NewsData.getAllNews() }
    var selectedCategoryLocal by remember(filterData.category) { mutableStateOf(filterData.category) }

    val startMillis = remember(filterData.startDate) { parseDateToUTCEpochMillis(filterData.startDate) }
    val endMillis = remember(filterData.endDate) { parseDateToUTCEpochMillis(filterData.endDate) }
    val unwantedLower = remember(filterData.unwantedWords) {
        filterData.unwantedWords.map { it.lowercase() }.toSet()
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
                val tag = when (category) {
                    "Sve" -> "filter_chip_all"
                    "Politika" -> "filter_chip_pol"
                    "Sport" -> "filter_chip_spo"
                    else -> ""
                }
                FilterChip(
                    onClick = { if (!isSelected) selectedCategoryLocal = category },
                    label = { Text(category) },
                    selected = isSelected,
                    leadingIcon = if (isSelected) { { Icon(Icons.Filled.Done, "Done") } } else null,
                    modifier = Modifier.testTag(tag).weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Nauka/tehnologija", "Ostalo").forEach { category ->
                val isSelected = selectedCategoryLocal == category
                val tag = when (category) {
                    "Nauka/tehnologija" -> "filter_chip_sci"
                    "Ostalo" -> "filter_chip_none"
                    else -> ""
                }
                FilterChip(
                    onClick = { if (!isSelected) selectedCategoryLocal = category },
                    label = { Text(category, maxLines = 1) },
                    selected = isSelected,
                    leadingIcon = if (isSelected) { { Icon(Icons.Filled.Done, "Done") } } else null,
                    modifier = Modifier.testTag(tag).weight(1f)
                )
            }
        }

        FilterChip(
            onClick = {
                val updatedFilter = FilterData(
                    category = selectedCategoryLocal,
                    startDate = filterData.startDate,
                    endDate = filterData.endDate,
                    unwantedWords = filterData.unwantedWords
                )
                val route = "filters?" +
                        "category=${URLEncoder.encode(updatedFilter.category, "UTF-8")}" +
                        "&startDate=${URLEncoder.encode(updatedFilter.startDate ?: "", "UTF-8")}" +
                        "&endDate=${URLEncoder.encode(updatedFilter.endDate ?: "", "UTF-8")}" +
                        "&unwanted=${URLEncoder.encode(updatedFilter.unwantedWords.joinToString(","), "UTF-8")}"

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
            val updatedFilter = filterData.copy(category = selectedCategoryLocal)
            NewsList(
                newsList = filteredNews,
                navController = navController,
                filterData = updatedFilter
            )
        }
    }
}
