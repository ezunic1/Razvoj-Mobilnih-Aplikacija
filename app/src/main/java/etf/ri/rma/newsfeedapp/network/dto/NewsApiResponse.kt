package etf.ri.rma.newsfeedapp.network.dto

data class NewsApiResponse(
    val meta: MetaData,
    val data: List<ApiNewsItem>?
)

data class MetaData(
    val found: Int,
    val returned: Int,
    val limit: Int,
    val page: Int
)
