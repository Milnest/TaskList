package com.milnest.tasklist.interactor

import android.os.AsyncTask
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.observer.Observable
import com.milnest.tasklist.other.utils.observer.Observer
import com.milnest.tasklist.data.web.Translator
import com.milnest.tasklist.data.web.YandexTranslate

class Translate private constructor(): Observable, AsyncTask<String, Void, Array<out String>>(){
    var translator: Translator = YandexTranslate.getYandexTranslateObj()!! //TODO:?

    var observer : Observer? = null
    override fun registerObserver(o: Observer) {
        observer = o
    }

    override fun removeObserver() {
        observer = null
    }

    override fun notifyObservers(title : String, text: String) {
        observer!!.update(title, text)
    }

    override fun doInBackground(vararg toTranslate: String): Array<out String>  {
        var translatedTitle = toTranslate[0] //TODO: 1 obrabotka
        var translatedText  = toTranslate[1]
        try {
            if (toTranslate[0] != "") translatedTitle = translator.translate("ru-en", toTranslate[0])
            if (toTranslate[1] != "") translatedText = translator.translate("ru-en", toTranslate[1])
            return arrayOf(translatedTitle, translatedText)
        } catch (ex: Exception) {
            return toTranslate
        }
    }

    override fun onPostExecute(data: Array<out String>) {
        super.onPostExecute(data)
        notifyObservers(data[0], data[1])
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