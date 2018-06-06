package com.milnest.tasklist.interactor

import android.os.AsyncTask
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.observer.Observable
import com.milnest.tasklist.other.utils.observer.Observer
import com.milnest.tasklist.data.web.Translator
import com.milnest.tasklist.data.web.YandexTranslate

class Translate private constructor(): Observable, AsyncTask<Task, Void, Task>(){
    var translator: Translator = YandexTranslate.getYandexTranslateObj()!! //TODO:?

    var observer : Observer? = null
    override fun registerObserver(o: Observer) {
        observer = o
    }

    override fun removeObserver() {
        observer = null
    }

    override fun notifyObservers(translatedText: Task) {
        observer!!.update(translatedText)
    }

    override fun doInBackground(vararg translatedText: Task): Task  {
        if (translatedText[0].title != "") translatedText[0].title = translator.translate("ru-en", translatedText[0].title)
        if (translatedText[0].data != "")translatedText[0].data = translator.translate("ru-en", translatedText[0].data)
        return translatedText[0]
        }

    override fun onPostExecute(data: Task) {
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