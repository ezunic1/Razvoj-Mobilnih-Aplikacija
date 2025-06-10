package etf.ri.rma.newsfeedapp.network

import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsAPI {
    private const val BASE_URL = "https://api.thenewsapi.com/v1/"

    val service: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java) // Retrofit servis za dohvat vijesti
    }
}
