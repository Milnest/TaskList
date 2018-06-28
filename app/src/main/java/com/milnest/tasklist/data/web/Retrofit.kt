package com.milnest.tasklist.data.web

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val retrofit = Retrofit.Builder()
        .baseUrl("https://translate.yandex.net")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

val YANDEX_API = retrofit.create<APIService>(APIService::class.java)
