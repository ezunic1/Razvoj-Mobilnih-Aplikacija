package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.data.network.api.ImageApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import java.util.concurrent.ConcurrentHashMap

class ImageDAO {

    private lateinit var apiService: ImageApiService
    private val tagCache = ConcurrentHashMap<String, List<String>>() // Keširam tagove po URL-u

    fun setApiService(service: ImageApiService) {
        this.apiService = service // Postavljam API servis koji ću koristiti
    }

    suspend fun getTags(imageURL: String): List<String> {
        if (!imageURL.startsWith("http")) throw InvalidImageURLException() // Provjeravam validnost URL-a
        return tagCache[imageURL] ?: run {
            val response = apiService.getTags(imageURL) // Pozivam API samo ako nemam tagove u kešu
            val tags = response.result.tags.mapNotNull { it.tag["en"] } // Uzimam engleske tagove iz odgovora
            tagCache[imageURL] = tags // Keširam rezultat
            tags
        }
    }
}
