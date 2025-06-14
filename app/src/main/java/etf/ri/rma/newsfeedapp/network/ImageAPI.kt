package etf.ri.rma.newsfeedapp.network

import etf.ri.rma.newsfeedapp.data.network.api.ImageApiService
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImageAPI {
    private const val BASE_URL = "https://api.imagga.com/"
    private const val API_KEY = "acc_b0633477e6650c1"
    private const val API_SECRET = "912545c7599196470a7d3198c59bcc87"

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
