package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.data.network.api.ImageApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import java.util.concurrent.ConcurrentHashMap

class ImageDAO {

    private lateinit var apiService: ImageApiService
    private val tagCache = ConcurrentHashMap<String, List<String>>()

    fun setApiService(service: ImageApiService) {
        this.apiService = service
    }

    suspend fun getTags(imageURL: String): List<String> {
        if (!imageURL.startsWith("http")) throw InvalidImageURLException()
        return tagCache[imageURL] ?: run {
            val response = apiService.getTags(imageURL)
            val tags = response.result.tags.mapNotNull { it.tag["en"] }
            tagCache[imageURL] = tags
            tags
        }
    }
}
