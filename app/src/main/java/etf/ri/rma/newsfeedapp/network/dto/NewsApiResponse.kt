package etf.ri.rma.newsfeedapp.network.dto

data class NewsApiResponse(
    val meta: MetaData, // Informacije o broju rezultata
    val data: List<ApiNewsItem>? // Lista vijesti sa servisa
)

data class MetaData(
    val found: Int,
    val returned: Int,
    val limit: Int,
    val page: Int // Informacije o paginaciji
)
