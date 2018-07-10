package com.milnest.tasklist

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.milnest.tasklist.data.web.Translator
import com.milnest.tasklist.data.web.YandexTranslate

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
        val translator : Translator = YandexTranslate.getYandexTranslateObj()!!
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

}