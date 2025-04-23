package etf.ri.rma.newsfeedapp.data

data class FilterData(
    val category: String = "Sve",
    val startDate: String? = null,
    val endDate: String? = null,
    val unwantedWords: List<String> = emptyList()
)
