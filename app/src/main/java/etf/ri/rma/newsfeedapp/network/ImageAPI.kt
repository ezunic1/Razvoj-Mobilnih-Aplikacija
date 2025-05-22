package etf.ri.rma.newsfeedapp.network

import etf.ri.rma.newsfeedapp.network.dto.ImageAPIService
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImageAPI {
    private const val BASE_URL = "https://api.imagga.com/"
    private const val API_KEY = "YOUR_API_KEY"
    private const val API_SECRET = "YOUR_API_SECRET"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", Credentials.basic(API_KEY, API_SECRET))
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ImageAPIService = retrofit.create(ImageAPIService::class.java)
}