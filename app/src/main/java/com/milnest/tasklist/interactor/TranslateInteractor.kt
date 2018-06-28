package com.milnest.tasklist.interactor

import com.google.gson.GsonBuilder
import com.milnest.tasklist.App
import com.milnest.tasklist.R
import com.milnest.tasklist.data.web.TranslateData
import com.milnest.tasklist.data.web.YANDEX_API
import io.reactivex.disposables.Disposable

class TranslateInteractor{
    val TRANSLATE_FAIL = App.context.resources.getString(R.string.translate_fail)
    private val gson = GsonBuilder().create()

    private var d: Disposable? = null

    fun run(title: String, text: String, transDirection: String, function: (TranslateData, Throwable?) -> Unit)  {
       val operation = YANDEX_API.translate("trnsl.1.1.20180420T121109Z.b002d3187929b557" +
                 ".b397db53cb8218077027dca1b19ad897ee594788", arrayListOf(title,text), transDirection)
        d = operation.subscribe(function)
    }

    fun dispose(){
        d?.dispose()
    }
}
