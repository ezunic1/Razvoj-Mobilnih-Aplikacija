package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun formatMillisDate(millis: Long?): String {
    if (millis == null) return ""
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = millis
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}

fun parseDateToUTCEpochMillis(dateString: String?): Long? {
    if (dateString.isNullOrBlank()) return null
    return try {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(dateString)?.time
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    navController: NavController,
    initialSearch: String = "",
    initialCategory: String,
    initialStartDate: String? = null,
    initialEndDate: String? = null,
    initialUnwantedWords: List<String> = emptyList(),
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = parseDateToUTCEpochMillis(initialStartDate),
        initialSelectedEndDateMillis = parseDateToUTCEpochMillis(initialEndDate)
    )
    val selectedStartDate = dateRangePickerState.selectedStartDateMillis
    val selectedEndDate = dateRangePickerState.selectedEndDateMillis
    val formattedStartDate = formatMillisDate(selectedStartDate)
    val formattedEndDate = formatMillisDate(selectedEndDate)
    val dateRangeDisplay = when {
        formattedStartDate.isNotEmpty() && formattedEndDate.isNotEmpty() -> "$formattedStartDate;$formattedEndDate"
        formattedStartDate.isNotEmpty() -> "$formattedStartDate;..."
        else -> "Odaberite opseg datuma"
    }

    var unwantedWordInput by remember { mutableStateOf("") }
    val unwantedWordsSet = remember {
        mutableStateOf(initialUnwantedWords.map { it.lowercase() }.toMutableSet())
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Sve", "Politika", "Sport").forEach { category ->
                        val selected = selectedCategory == category
                        FilterChip(
                            onClick = { if (!selected) selectedCategory = category },
                            label = { Text(category) },
                            selected = selected,
                            leadingIcon = if (selected) { { Icon(Icons.Filled.Done, "Done") } } else null,
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

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Nauka/tehnologija", "Ostalo").forEach { category ->
                        val selected = selectedCategory == category
                        FilterChip(
                            onClick = { if (!selected) selectedCategory = category },
                            label = { Text(category, maxLines = 1) },
                            selected = selected,
                            leadingIcon = if (selected) { { Icon(Icons.Filled.Done, "Done") } } else null,
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

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Datum objave", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateRangeDisplay,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .testTag("filter_daterange_display"),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { showDatePickerDialog = true },
                        modifier = Modifier.testTag("filter_daterange_button")
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Odaberi datum")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Nepoželjne riječi", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = unwantedWordInput,
                        onValueChange = { unwantedWordInput = it },
                        label = { Text("Unesite riječ...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("filter_unwanted_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val wordToAdd = unwantedWordInput.trim()
                            if (wordToAdd.isNotEmpty()) {
                                unwantedWordsSet.value = unwantedWordsSet.value.toMutableSet().apply {
                                    add(wordToAdd.lowercase())
                                }
                                unwantedWordInput = ""
                            }
                        },
                        modifier = Modifier.testTag("filter_unwanted_add_button"),
                        enabled = unwantedWordInput.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Dodaj riječ")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("filter_unwanted_list"),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        //.padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (unwantedWordsSet.value.isEmpty()) {
                        Text("Nema dodanih riječi.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        unwantedWordsSet.value.sorted().forEach { word ->
                            AssistChip(
                                modifier = Modifier.height(30.dp),
                                onClick = {},
                                label = { Text(word, style = MaterialTheme.typography.bodyMedium) },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        unwantedWordsSet.value = unwantedWordsSet.value.toMutableSet().apply {
                                            remove(word)
                                        }
                                    }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Ukloni riječ")
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showDatePickerDialog) {
            Dialog(onDismissRequest = { showDatePickerDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        DateRangePicker(
                            state = dateRangePickerState,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("filter_daterange_picker")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDatePickerDialog = false }) {
                                Text("Otkaži")
                            }
                           Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = {
                                showDatePickerDialog = false
                            }) {
                                Text("Potvrdi")
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val categoryToPass = selectedCategory
                val startDateToPass = formatMillisDate(selectedStartDate)
                val endDateToPass = formatMillisDate(selectedEndDate)
                val unwantedWordsToPass = unwantedWordsSet.value.joinToString(separator = ",")

                val encodedCategory = URLEncoder.encode(categoryToPass, StandardCharsets.UTF_8.toString())
                val encodedStartDate = URLEncoder.encode(startDateToPass, StandardCharsets.UTF_8.toString())
                val encodedEndDate = URLEncoder.encode(endDateToPass, StandardCharsets.UTF_8.toString())
                val encodedUnwanted = URLEncoder.encode(unwantedWordsToPass, StandardCharsets.UTF_8.toString())

                val route = "home?" +
                        "category=$encodedCategory" +
                        "&startDate=$encodedStartDate" +
                        "&endDate=$encodedEndDate" +
                        "&unwanted=$encodedUnwanted"

                navController.navigate(route) {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .testTag("filter_apply_button")
        ) {
            Text("Primijeni filtere")
        }
    }
    BackHandler {
        val route = "home?" +
                "category=${URLEncoder.encode(initialCategory, "UTF-8")}" +
                "&startDate=${URLEncoder.encode(initialStartDate ?: "", "UTF-8")}" +
                "&endDate=${URLEncoder.encode(initialEndDate ?: "", "UTF-8")}" +
                "&unwanted=${URLEncoder.encode(initialUnwantedWords.joinToString(","), "UTF-8")}"

        navController.navigate(route) {
            popUpTo("home") { inclusive = true }
        }
    }

}
