package etf.ri.rma.newsfeedapp.network

import etf.ri.rma.newsfeedapp.network.dto.NewsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path

interface NewsApiService {

    @GET("news/top")
    suspend fun getTopNews(
        @Query("api_token") apiKey: String,
        @Query("locale") locale: String,
        @Query("categories") category: String,
        @Query("limit") limit: Int
    ): NewsApiResponse


    @GET("news/all")
    suspend fun getAllNews(
        @Query("api_token") apiKey: String,
        @Query("language") language: String,
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

object NewsAPI {
    private const val BASE_URL = "https://api.thenewsapi.com/v1/"

    val service: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}