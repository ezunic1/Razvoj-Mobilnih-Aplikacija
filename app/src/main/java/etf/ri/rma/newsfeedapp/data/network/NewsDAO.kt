package etf.ri.rma.newsfeedapp.data.network

import android.util.Log
import etf.ri.rma.newsfeedapp.dao.SavedNewsDAO
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.model.*
import etf.ri.rma.newsfeedapp.network.NewsAPI
import etf.ri.rma.newsfeedapp.network.dto.ApiNewsItem
import etf.ri.rma.newsfeedapp.network.dto.NewsApiResponse

class NewsDAO(private val savedNewsDAO: SavedNewsDAO) {

    private val allNews = LinkedHashMap<String, NewsItem>()
    private val newsOrder = mutableListOf<String>()
    private val categoryTimestamps = mutableMapOf<String, Long>()
    private val featuredCache = mutableMapOf<String, List<String>>()
    private val similarNewsCache = mutableMapOf<String, List<NewsItem>>()

    private var apiService: NewsApiService = NewsAPI.service
    private var apiKey: String = "jt4GRCo8rQRXn41zkMBPhDKl9MMptg1qdJaSIo8Y"
    private val locale = "us"

    fun setApiService(service: NewsApiService) {
        apiService = service
    }

    fun setApiKey(token: String) {
        apiKey = token
    }

    private fun insertOnTop(item: NewsItem) {
        allNews[item.uuid] = item
        newsOrder.remove(item.uuid)
        newsOrder.add(0, item.uuid)
    }

    suspend fun getTopStoriesByCategory(category: String, limit: Int = 3): List<NewsItem> {
        val now = System.currentTimeMillis()
        val lastFetched = categoryTimestamps[category] ?: 0L
        val existing = allNews.values.filter { it.category.equals(category, ignoreCase = true) }

        if (now - lastFetched < 30_000 && existing.isNotEmpty()) {
            val featuredIds = featuredCache[category].orEmpty()
            val featuredNews = existing.filter { it.uuid in featuredIds }.map { it.copy(isFeatured = true) }
            val standardNews = existing.filterNot { it.uuid in featuredIds }.map { it.copy(isFeatured = false) }
            return featuredNews + standardNews
        }

        return try {
            val response: NewsApiResponse = apiService.getTopNews(apiKey, locale, category, limit)
            val newItems = response.data?.map { it.toNewsItem(forcedCategory = category) } ?: emptyList()

            val newFeatured = mutableListOf<NewsItem>()

            for (item in newItems) {
                val prepared = item.copy(isFeatured = true)
                insertOnTop(prepared)

                val saved = saveNewsToDb(prepared)
                if (saved && !prepared.imageUrl.isNullOrBlank()) {
                    try {
                        val tags = etf.ri.rma.newsfeedapp.data.network.ImageDAO().apply {
                            setApiService(etf.ri.rma.newsfeedapp.network.ImageAPI.service)
                        }.getTags(prepared.imageUrl)
                        addTagsToNews(tags, prepared.uuid)
                    } catch (e: Exception) {
                        Log.e("NewsDAO", "Image tag error: ${e.message}")
                    }
                }

                newFeatured.add(prepared)
            }

            categoryTimestamps[category] = now
            featuredCache[category] = newFeatured.map { it.uuid }

            val nonFeatured = newsOrder.mapNotNull { allNews[it] }
                .filter { it.category.equals(category, ignoreCase = true) && it.uuid !in featuredCache[category].orEmpty() }
                .map { it.copy(isFeatured = false) }

            newFeatured + nonFeatured
        } catch (e: Exception) {
            Log.e("NewsDAO", "Network error: ${e.message}")
            existing
        }
    }

    fun getAllStories(): List<NewsItem> {
        return newsOrder.mapNotNull { allNews[it] }
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        similarNewsCache[uuid]?.let { return it }
        return try {
            val response = apiService.getSimilarNews(uuid, apiKey, language = "en")
            val result = response.data?.map { it.toNewsItem() }?.filter { it.uuid != uuid }?.take(2) ?: emptyList()
            result.forEach { if (!allNews.containsKey(it.uuid)) insertOnTop(it) }
            similarNewsCache[uuid] = result
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun ApiNewsItem.toNewsItem(forcedCategory: String? = null): NewsItem {
        val chosenCategory = forcedCategory ?: (categories.firstOrNull() ?: "Ostalo")
        return NewsItem(
            uuid = uuid,
            title = title,
            snippet = description ?: "",
            imageUrl = image_url,
            category = chosenCategory,
            isFeatured = false,
            source = source,
            publishedDate = published_at.take(10).split("-").reversed().joinToString("-"),
            imageTags = arrayListOf()
        )
    }

    suspend fun saveNewsToDb(item: NewsItem): Boolean {
        if (savedNewsDAO.getNewsByUUID(item.uuid) != null) return false
        val entity = NewsEntity(
            uuid = item.uuid,
            title = item.title,
            snippet = item.snippet,
            imageUrl = item.imageUrl,
            category = item.category,
            isFeatured = item.isFeatured,
            source = item.source,
            publishedDate = item.publishedDate
        )
        val result = savedNewsDAO.saveNews(entity)
        return result != -1L
    }

    suspend fun addTagsToNews(tags: List<String>, newsUuid: String): Int {
        val newsWithTags = savedNewsDAO.getNewsByUUID(newsUuid) ?: return 0
        val newsId = newsWithTags.news.id
        var newTags = 0
        for (tag in tags) {
            val newTagId = savedNewsDAO.insertTag(TagEntity(value = tag)).toInt()
            if (newTagId != -1) newTags++
            val actualId = savedNewsDAO.getTagsByValue(listOf(tag)).firstOrNull()?.id ?: newTagId
            savedNewsDAO.insertNewsTagCrossRef(NewsTagsCrossRef(newsId, actualId))
        }
        return newTags
    }

    suspend fun getNewsFromDbByCategory(category: String): List<NewsItem> {
        val newsWithTags = savedNewsDAO.getNewsWithCategory(category)
        return newsWithTags.map {
            NewsItem(
                uuid = it.news.uuid,
                title = it.news.title,
                snippet = it.news.snippet,
                imageUrl = it.news.imageUrl,
                category = it.news.category,
                isFeatured = it.news.isFeatured,
                source = it.news.source,
                publishedDate = it.news.publishedDate,
                imageTags = ArrayList(it.tags.map { tag -> tag.value })
            )
        }
    }

    suspend fun getAllNewsFromDb(): List<NewsItem> {
        val newsWithTags = savedNewsDAO.allNews()
        return newsWithTags.map {
            NewsItem(
                uuid = it.news.uuid,
                title = it.news.title,
                snippet = it.news.snippet,
                imageUrl = it.news.imageUrl,
                category = it.news.category,
                isFeatured = it.news.isFeatured,
                source = it.news.source,
                publishedDate = it.news.publishedDate,
                imageTags = ArrayList(it.tags.map { tag -> tag.value })
            )
        }
    }

    suspend fun preloadNewsFromDb() {
        val cachedNews = getAllNewsFromDb()
        for (item in cachedNews) {
            if (!allNews.containsKey(item.uuid)) {
                allNews[item.uuid] = item
                newsOrder.add(item.uuid)
            }
        }
    }

}
