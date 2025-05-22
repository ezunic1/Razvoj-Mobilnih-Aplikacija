package etf.ri.rma.newsfeedapp.network.dto

import etf.ri.rma.newsfeedapp.network.dto.ApiNewsItem

data class NewsApiResponse(
    val data: List<ApiNewsItem>
)