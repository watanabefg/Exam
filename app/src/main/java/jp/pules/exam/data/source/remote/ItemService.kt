package jp.pules.exam.data.source.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import jp.pules.exam.items.domain.model.Item

interface ItemService {
    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     */
    @GET("items")
    fun getItems(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<Item>>

    @GET("items/{id}")
    fun getItem(
            @Path("id") entryId: String
    ): Call<Item>
}