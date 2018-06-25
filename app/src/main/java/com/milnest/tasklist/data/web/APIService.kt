package com.milnest.tasklist.data.web

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query


interface APIService {
    @POST("translate")
    fun translate(@Query("key") translate: String, @Query("text") text: String, @Query("&lang") lang : String ): Call<TranslateData>
}