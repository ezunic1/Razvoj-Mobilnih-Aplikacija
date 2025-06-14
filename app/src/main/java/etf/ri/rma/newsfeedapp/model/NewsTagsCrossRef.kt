package etf.ri.rma.newsfeedapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NewsTags", primaryKeys = ["newsId", "tagId"])
data class NewsTagsCrossRef(
    val newsId: Int,
    val tagId: Int
)
