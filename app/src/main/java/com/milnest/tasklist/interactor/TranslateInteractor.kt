package com.milnest.tasklist.interactor

import android.os.AsyncTask
import com.milnest.tasklist.App
import com.milnest.tasklist.other.utils.observer.Observable
import com.milnest.tasklist.other.utils.observer.Observer


class TranslateInteractor : Observable, AsyncTask<String, Void, Array<out String>>(){

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
        var translatedTitle = toTranslate[0]
        var translatedText  = toTranslate[1]
        return try {
            if (toTranslate[0] != "") translatedTitle = App.translator.translate("ru-en", toTranslate[0])
            if (toTranslate[1] != "") translatedText = App.translator.translate("ru-en", toTranslate[1])
            arrayOf(translatedTitle, translatedText)
        } catch (ex: Exception) {
            toTranslate
        }
    }

    override fun onPostExecute(data: Array<out String>) {
        super.onPostExecute(data)
        notifyObservers(data[0], data[1])
    }
}