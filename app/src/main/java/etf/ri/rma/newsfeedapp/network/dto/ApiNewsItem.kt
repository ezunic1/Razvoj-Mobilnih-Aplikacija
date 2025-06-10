package etf.ri.rma.newsfeedapp.network.dto
data class ApiNewsItem(
    val uuid: String,
    val title: String,
    val description: String = "",
    val url: String,
    val image_url: String? = null,
    val categories: List<String> = emptyList(),
    val source: String = "",
    val published_at: String = ""
)
