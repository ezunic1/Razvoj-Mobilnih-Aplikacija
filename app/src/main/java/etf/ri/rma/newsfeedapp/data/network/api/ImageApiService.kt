package etf.ri.rma.newsfeedapp.data.network.api

import etf.ri.rma.newsfeedapp.network.dto.ImageTagResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApiService {
    @GET("v2/tags")
    suspend fun getTags(@Query("image_url") imageURL: String): ImageTagResponse
}
