package com.milnest.tasklist.data.web

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query


interface APIService {
    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    fun translate(@FieldMap map: Map<String, String>) : Call<Any>
    //fun translate(@Query("key") translate: String, @Query("text") text: String, @Query("&lang") lang : String ): Call<TranslateData>
}