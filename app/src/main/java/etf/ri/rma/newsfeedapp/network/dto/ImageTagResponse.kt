package etf.ri.rma.newsfeedapp.network.dto

data class ImageTagResponse(
    val result: TagResult
)

data class TagResult(
    val tags: List<TagEntry>
)

data class TagEntry(
    val tag: Map<String, String>,
    val confidence: Double
)
