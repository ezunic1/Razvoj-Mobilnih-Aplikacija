package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.network.ImageAPI
import etf.ri.rma.newsfeedapp.util.InvalidImageURLException
import java.util.concurrent.ConcurrentHashMap

class ImageDAO {
    private val tagCache = ConcurrentHashMap<String, List<String>>()

    suspend fun getTags(imageURL: String): List<String> {
        if (!imageURL.startsWith("http")) throw InvalidImageURLException()
        return tagCache[imageURL] ?: run {
            val response = ImageAPI.service.getTags(imageURL)
            val tags = response.result.tags.mapNotNull { it.tag["en"] }
            tagCache[imageURL] = tags
            tags
        }
    }
}
