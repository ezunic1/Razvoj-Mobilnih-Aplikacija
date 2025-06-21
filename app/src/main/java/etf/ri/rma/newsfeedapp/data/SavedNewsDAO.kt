package etf.ri.rma.newsfeedapp.data

import androidx.room.*
import etf.ri.rma.newsfeedapp.model.*

@Dao
interface SavedNewsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: NewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTagCrossRef(crossRef: NewsTagsCrossRef): Long

    @Query("SELECT * FROM Tags WHERE value IN (:tags)")
    suspend fun getTagsByValue(tags: List<String>): List<TagEntity>

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun getAllNewsInternal(): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun getNewsWithCategoryInternal(category: String): List<NewsWithTags>

    @Transaction
    @Query("""
        SELECT * FROM News
        INNER JOIN NewsTags ON News.id = NewsTags.newsId
        INNER JOIN Tags ON Tags.id = NewsTags.tagId
        WHERE Tags.value IN (:tags)
        GROUP BY News.id
        ORDER BY publishedDate DESC
    """)
    suspend fun getSimilarNewsInternal(tags: List<String>): List<NewsWithTags>

    @Transaction
    @Query("""
        SELECT Tags.value FROM Tags
        INNER JOIN NewsTags ON Tags.id = NewsTags.tagId
        WHERE NewsTags.newsId = :newsId
    """)
    suspend fun getTags(newsId: Int): List<String>

    @Query("SELECT id FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsIdByUUID(uuid: String): Int?

    @Query("SELECT EXISTS(SELECT 1 FROM News WHERE uuid = :uuid LIMIT 1)")
    suspend fun exists(uuid: String): Boolean

    suspend fun saveNews(news: NewsItem): Boolean {
        if (exists(news.uuid)) return false

        val newsEntity = NewsEntity(
            uuid = news.uuid,
            title = news.title,
            snippet = news.snippet,
            imageUrl = news.imageUrl,
            category = news.category,
            isFeatured = news.isFeatured,
            source = news.source,
            publishedDate = news.publishedDate
        )
        return insertNews(newsEntity) != -1L
    }

    suspend fun allNews(): List<NewsItem> {
        return getAllNewsInternal().map { it.toNewsItem() }
    }

    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        return getNewsWithCategoryInternal(category).map { it.toNewsItem() }
    }

    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newTags = 0
        for (tag in tags) {
            val tagEntity = TagEntity(value = tag)
            val tagId = insertTag(tagEntity).toInt().takeIf { it != -1 }
                ?: getTagsByValue(listOf(tag)).first().id
            val crossRef = NewsTagsCrossRef(newsId = newsId, tagId = tagId)
            insertNewsTagCrossRef(crossRef)
            if (tagId == tagEntity.id) newTags++
        }
        return newTags
    }

    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val limitedTags = tags.take(2)
        return getSimilarNewsInternal(limitedTags).map { it.toNewsItem() }
    }
}
