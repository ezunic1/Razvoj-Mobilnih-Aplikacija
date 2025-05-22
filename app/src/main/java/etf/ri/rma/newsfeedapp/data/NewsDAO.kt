package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.network.NewsAPI
import etf.ri.rma.newsfeedapp.network.dto.ApiNewsItem
import etf.ri.rma.newsfeedapp.util.InvalidUUIDException
import java.util.*

object NewsDAO {

    private val allNews = LinkedHashMap<String, NewsItem>()
    private val categoryTimestamps = mutableMapOf<String, Long>()
    private val featuredCache = mutableMapOf<String, List<String>>()

    private val apiKey = "7SyaeyIBhk9LyYR7VCtUS9Uu600uAPH7rdkzduTw"
    private val locale = "us"

    init {
        val initialNews = listOf(
            NewsItem("uuid-1", "SpaceX launches new rocket", "Elon Musk's company sends new payload into orbit.", null, "science", false, "CNN", "18-05-2025", arrayListOf()),
            NewsItem("uuid-2", "AI beats human in chess", "AI breaks new record in competitive chess match.", null, "tech", false, "BBC", "14-05-2025", arrayListOf()),
            NewsItem("uuid-3", "Economic growth hits new high", "GDP grows faster than expected.", null, "business", false, "Reuters", "10-05-2025", arrayListOf()),
            NewsItem("uuid-4", "Sports league finals", "The championship game ends in a dramatic finish.", null, "sports", false, "ESPN", "11-05-2025", arrayListOf()),
            NewsItem("uuid-5", "Global health summit", "Leaders discuss pandemic preparedness.", null, "health", false, "WHO News", "09-05-2025", arrayListOf()),
            NewsItem("uuid-6", "Oscar winners announced", "Big wins at the annual movie awards.", null, "entertainment", false, "Variety", "12-05-2025", arrayListOf()),
            NewsItem("uuid-7", "New policies debated", "Parliament discusses reforms.", null, "politics", false, "Al Jazeera", "13-05-2025", arrayListOf()),
            NewsItem("uuid-8", "Best food of the year", "Restaurants voted best dishes.", null, "food", false, "Food Network", "16-05-2025", arrayListOf()),
            NewsItem("uuid-9", "Summer travel tips", "How to travel smart this season.", null, "travel", false, "Lonely Planet", "17-05-2025", arrayListOf()),
            NewsItem("uuid-10", "Quantum computers", "A beginner's guide to the future of computing.", null, "science", false, "Nature", "15-05-2025", arrayListOf())
        )
        for (news in initialNews) {
            allNews[news.uuid] = news
        }
    }
    suspend fun getTopStoriesByCategory(category: String, limit: Int = 3): List<NewsItem> {
        val now = System.currentTimeMillis()
        val lastFetched = categoryTimestamps[category] ?: 0L
        println("Category: $category, lastFetched: $lastFetched, now: $now")
        val existing = allNews.values.filter {
            it.category.equals(category, ignoreCase = true)
        }

        return if (now - lastFetched < 0 && existing.isNotEmpty()) {//30_000 za 30 sekundi
            val featuredIds = featuredCache[category].orEmpty()
            existing.map { it.copy(isFeatured = it.uuid in featuredIds) }
        } else {
            try {
                val response = NewsAPI.service.getTopNews(
                    apiKey = apiKey,
                    locale = locale,
                    category = category,
                    limit = limit
                )
                val newItems = response.data?.map { it.toNewsItem() } ?: emptyList()

                val newFeatured = mutableListOf<NewsItem>()
                for (item in newItems) {
                    val existingItem = allNews[item.uuid]
                    if (existingItem == null) {
                        allNews[item.uuid] = item.copy(isFeatured = true)
                        newFeatured.add(item.copy(isFeatured = true))
                    } else {
                        allNews[item.uuid] = existingItem.copy(isFeatured = true)
                        newFeatured.add(existingItem.copy(isFeatured = true))
                    }
                }

                categoryTimestamps[category] = now
                featuredCache[category] = newFeatured.map { it.uuid }

                val nonFeatured = allNews.values
                    .filter {
                        it.category.equals(category, ignoreCase = true) &&
                                it.uuid !in featuredCache[category].orEmpty()
                    }
                    .map { it.copy(isFeatured = false) }

                newFeatured + nonFeatured
            } catch (e: Exception) {
                e.printStackTrace()
                allNews.values.filter {
                    it.category.equals(category, ignoreCase = true)
                }
            }
        }

    }

    fun getAllStories(): List<NewsItem> {
        return allNews.values.toList()
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!uuid.matches(Regex("^[a-fA-F0-9\\-]{36}|uuid-[0-9]+$"))) throw InvalidUUIDException()

        val baseItem = allNews[uuid] ?: return listOf()
        val sameCategory = allNews.values.filter { it.category == baseItem.category && it.uuid != uuid }

        return sameCategory
            .sortedByDescending { similarity(it.title, baseItem.title) }
            .take(2)
    }

    private fun ApiNewsItem.toNewsItem(): NewsItem {
        return NewsItem(
            uuid = uuid,
            title = title,
            snippet = description ?: "",
            imageUrl = image_url,
            category = categories.firstOrNull() ?: "Ostalo",
            isFeatured = false,
            source = source,
            publishedDate = published_at.take(10).split("-").reversed().joinToString("-"),
            imageTags = arrayListOf()
        )
    }

    private fun similarity(a: String, b: String): Int {
        val aWords = a.lowercase(Locale.getDefault()).split(" ").toSet()
        val bWords = b.lowercase(Locale.getDefault()).split(" ").toSet()
        return aWords.intersect(bWords).size
    }
}
