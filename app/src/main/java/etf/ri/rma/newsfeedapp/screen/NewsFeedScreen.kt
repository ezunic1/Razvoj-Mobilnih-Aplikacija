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
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.net.URLEncoder

// Provjerava da li je datum objave unutar selektovanog vremenskog opsega
fun isWithinSelectedRange(publishedDate: String, startMillis: Long?, endMillis: Long?): Boolean {
    if (startMillis == null && endMillis == null) return true
    return try {
        val formatter = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
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
    filterData: FilterData,
    newsDAO: NewsDAO
) {
    // Interno selektovana kategorija
    var selectedCategoryLocal by remember(filterData.category) { mutableStateOf(filterData.category) }

    // Mapa kategorija iz prikaza u API ključ
    val categoryMap = mapOf(
        "Sve" to null,
        "Politika" to "politics",
        "Sport" to "sports",
        "Nauka" to "science",
        "Tehnologija" to "tech",
        "Ostalo" to "general"
    )
    val apiCategory = categoryMap[selectedCategoryLocal]

    // Parsiranje početnog i krajnjeg datuma
    val startMillis = remember(filterData.startDate) { parseDateToUTCEpochMillis(filterData.startDate) }
    val endMillis = remember(filterData.endDate) { parseDateToUTCEpochMillis(filterData.endDate) }
    val unwantedLower = remember(filterData.unwantedWords) {
        filterData.unwantedWords.map { it.lowercase() }.toSet()
    }

    // Dohvata sve vijesti iz API-ja ili cache-a na osnovu kategorije
    val allNewsItems by produceState(initialValue = emptyList<NewsItem>(), selectedCategoryLocal) {
        value = try {
            if (apiCategory == null) {
                newsDAO.getAllStories()
            } else {
                newsDAO.getTopStoriesByCategory(apiCategory)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Filtrira vijesti po kategoriji, datumu i neželjenim riječima
    val filteredNews = remember(selectedCategoryLocal, startMillis, endMillis, unwantedLower, allNewsItems) {
        allNewsItems.filter { newsItem ->
            val categoryMatch =
                selectedCategoryLocal == "Sve" ||
                        newsItem.category.equals(apiCategory, ignoreCase = true)
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

    // Glavni UI layout
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Prva linija kategorija
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
                    onClick = { selectedCategoryLocal = category },
                    label = { Text(category) },
                    selected = isSelected,
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Filled.Done, "Done") }
                    } else null,
                    modifier = Modifier.testTag(tag).weight(1f)
                )
            }
        }

        // Druga linija kategorija
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Nauka", "Tehnologija", "Ostalo").forEach { category ->
                val isSelected = selectedCategoryLocal == category
                val tag = when (category) {
                    "Nauka" -> "filter_chip_nauka"
                    "Tehnologija" -> "filter_chip_tech"
                    "Ostalo" -> "filter_chip_none"
                    else -> ""
                }
                FilterChip(
                    onClick = { selectedCategoryLocal = category },
                    label = { Text(category, maxLines = 1) },
                    selected = isSelected,
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Filled.Done, "Done") }
                    } else null,
                    modifier = Modifier.testTag(tag).weight(1f)
                )
            }
        }

        // Dugme za dodatne filtere
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

        // Prikaz poruke ako nema vijesti
        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji $selectedCategoryLocal")
        } else {
            // Inače, prikaži listu vijesti
            val updatedFilter = filterData.copy(category = selectedCategoryLocal)
            NewsList(
                newsList = filteredNews,
                navController = navController,
                filterData = updatedFilter
            )
        }
    }
}
