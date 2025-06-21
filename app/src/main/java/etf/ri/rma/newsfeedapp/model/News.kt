package etf.ri.rma.newsfeedapp.model

data class News(
    val id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String,
    val tags: List<TagEntity> = emptyList()
)
