package etf.ri.rma.newsfeedapp.network

import etf.ri.rma.newsfeedapp.data.network.api.ImageApiService
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImageAPI {
    private const val BASE_URL = "https://api.imagga.com/"
    private const val API_KEY = "acc_10900de1e336733"
    private const val API_SECRET = "19310bce3a35fb06152773bc69a2eb42"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", Credentials.basic(API_KEY, API_SECRET))
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val service: ImageApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageApiService::class.java)
    }
}
