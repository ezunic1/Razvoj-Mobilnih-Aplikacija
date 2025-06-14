package etf.ri.rma.newsfeedapp.dao

import androidx.room.*
import etf.ri.rma.newsfeedapp.model.*

@Dao
interface SavedNewsDAO {

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun allNews(): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun getNewsWithCategory(category: String): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsByUUID(uuid: String): NewsWithTags?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveNews(news: NewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTagCrossRef(crossRef: NewsTagsCrossRef): Long

    @Query("SELECT * FROM Tags WHERE value IN (:tags)")
    suspend fun getTagsByValue(tags: List<String>): List<TagEntity>

    @Transaction
    @Query("""
        SELECT * FROM News
        INNER JOIN NewsTags ON News.id = NewsTags.newsId
        INNER JOIN Tags ON Tags.id = NewsTags.tagId
        WHERE Tags.value IN (:tags)
        GROUP BY News.id
        ORDER BY publishedDate DESC
    """)
    suspend fun getSimilarNews(tags: List<String>): List<NewsWithTags>

    @Transaction
    @Query("""
        SELECT Tags.value FROM Tags
        INNER JOIN NewsTags ON Tags.id = NewsTags.tagId
        WHERE NewsTags.newsId = :newsId
    """)
    suspend fun getTags(newsId: Int): List<String>

    @Transaction
    @Query("SELECT EXISTS(SELECT 1 FROM News WHERE uuid = :uuid LIMIT 1)")
    suspend fun existsNews(uuid: String): Boolean
}
