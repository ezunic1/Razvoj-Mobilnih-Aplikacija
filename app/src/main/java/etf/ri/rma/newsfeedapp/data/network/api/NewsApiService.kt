package etf.ri.rma.newsfeedapp.data.network.api

import etf.ri.rma.newsfeedapp.network.dto.NewsApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {
    @GET("news/top")
    suspend fun getTopNews(
        @Query("api_token") apiKey: String,
        @Query("locale") locale: String,
        @Query("categories") category: String,
        @Query("limit") limit: Int
    ): NewsApiResponse

    @GET("news/similar/{uuid}")
    suspend fun getSimilarNews(
        @Path("uuid") uuid: String,
        @Query("api_token") apiKey: String,
        @Query("language") language: String,
        @Query("published_on") publishedOn: String
    ): NewsApiResponse
}
