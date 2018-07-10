package com.milnest.tasklist.data.web

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query


interface APIService {
    @POST("/api/v1.5/tr.json/translate")
    fun translate(@Query("key") key: String,
                  @Query("text") text: List<String>,
                  @Query("lang") lang : String ): Single<TranslateData>
}