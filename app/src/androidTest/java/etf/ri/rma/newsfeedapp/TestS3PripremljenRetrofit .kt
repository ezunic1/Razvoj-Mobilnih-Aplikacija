package etf.ri.rma.newsfeedapp

import etf.ri.rma.newsfeedapp.data.network.ImageDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.data.network.api.ImageApiService
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/*
class TestS3PripremljenRetrofit {
    fun getNewsDAOwithBaseURL(baseURL: String, httpClient: OkHttpClient): NewsDAO {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val newsApiService = retrofit.create(NewsApiService::class.java)
        val newsDAO = NewsDAO()
        newsDAO.setApiService(newsApiService)
        return newsDAO
    }

    fun getImaggaDAOwithBaseURL(baseURL: String, httpClient: OkHttpClient): ImageDAO {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val imageApiService = retrofit.create(ImageApiService::class.java)
        val imageDAO = ImageDAO()
        imageDAO.setApiService(imageApiService)
        return imageDAO
    }
}
*/