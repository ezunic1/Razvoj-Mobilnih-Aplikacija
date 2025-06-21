package etf.ri.rma.newsfeedapp.model

fun NewsWithTags.toNewsItem(): NewsItem {
    return NewsItem(
        uuid = news.uuid,
        title = news.title,
        snippet = news.snippet,
        imageUrl = news.imageUrl,
        category = news.category,
        isFeatured = news.isFeatured,
        source = news.source,
        publishedDate = news.publishedDate,
        imageTags = tags
    )
}

