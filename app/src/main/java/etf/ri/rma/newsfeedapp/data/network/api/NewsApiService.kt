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
    ): NewsApiResponse // Dohvatanje top vijesti po kategoriji

    @GET("news/similar/{uuid}")
    suspend fun getSimilarNews(
        @Path("uuid") uuid: String,
        @Query("api_token") apiKey: String,
        @Query("language") language: String? = null,
        @Query("published_on") publishedOn: String? = null
    ): NewsApiResponse // Dohvatanje sličnih vijesti na osnovu UUID-a


    @GET("news/similar/{uuid}")
    suspend fun getSimilarWithSource(
        @Path("uuid") uuid: String,
        @Query("api_token") apiKey: String,
        @Query("limit") limit: Int = 5
    ): NewsApiResponse

}
