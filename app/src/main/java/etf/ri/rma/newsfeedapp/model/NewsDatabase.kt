package etf.ri.rma.newsfeedapp.model

import android.content.Context
import androidx.room.*
import etf.ri.rma.newsfeedapp.dao.SavedNewsDAO

@Database(
    entities = [NewsEntity::class, TagEntity::class, NewsTagsCrossRef::class],
    version = 1
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun savedNewsDAO(): SavedNewsDAO

    companion object {
        @Volatile private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
