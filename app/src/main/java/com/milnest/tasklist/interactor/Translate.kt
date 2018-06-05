package com.milnest.tasklist.interactor

import android.os.AsyncTask
import com.milnest.tasklist.entities.TextActData
import com.milnest.tasklist.entities.observer.Observable
import com.milnest.tasklist.entities.observer.Observer
import java.io.IOException

class Translate private constructor(): Observable, AsyncTask<TextActData, Void, TextActData?>(){
    var observer : Observer? = null
    override fun registerObserver(o: Observer) {
        observer = o
    }

    override fun removeObserver() {
        observer = null
    }

    override fun notifyObservers(translatedText: TextActData?) {
        observer!!.update(translatedText)
    }

    override fun doInBackground(vararg params: TextActData): TextActData? = try {
        TextActData(YandexTranslate.translate("ru-en",
                params[0].taskTitle), YandexTranslate.translate("ru-en",
                params[0].taskText) )
        } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    override fun onPostExecute(data: TextActData?) {
        super.onPostExecute(data)
        notifyObservers(data)
    }

    companion object {
        var translate:Translate? = null
        fun getTranslateObj(): Translate? {
            if (translate == null) {
                translate = Translate()
            }
            return translate
        }
        fun delTranslateObj() {
            translate = null
        }
    }
}