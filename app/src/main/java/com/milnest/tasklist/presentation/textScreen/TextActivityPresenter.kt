package com.milnest.tasklist.presentation.textScreen

import android.content.Intent
import android.os.Bundle
import com.milnest.tasklist.R
import com.milnest.tasklist.IntentData
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.observer.Observer
import com.milnest.tasklist.interactor.Translate
import com.milnest.tasklist.data.repository.DBRepository
import java.lang.Exception
import java.lang.ref.WeakReference

class TextActivityPresenter : Observer {
    var textId: Int = -1
    private val savedIntent = Intent()
    private lateinit var view: WeakReference<TextActInterface>

    fun setStartText(extras: Bundle?) {
        if (extras != null){
            textId = extras.getInt(IntentData.ID)
            val textTask = DBRepository.getTaskById(textId) as Task
            view.get()?.setText(textTask.title, textTask.data)
        }
    }

    fun saveClicked(title : String, text:String) {
        savedIntent.putExtra(IntentData.NAME, title)
        savedIntent.putExtra(IntentData.TEXT, text)
        if (textId != -1) {
            savedIntent.putExtra(IntentData.ID, textId)
        }
        closeView()
    }

    fun shareClicked(title : String, text:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        view.get()?.startShareAct(shareIntent)
    }

    fun translationClicked(title : String, text:String) {
        /*if (TranslateInteractor.observer == null) {
            TranslateInteractor.registerObserver(this)
        }
        *//*AsyncRequest().execute(textActData)*//*
        TranslateInteractor.execute(textActData)
        TranslateInteractor = null*/
        val translate = Translate.getTranslateObj()
        if (translate!!.observer == null) {
            translate.registerObserver(this)
        }
        translate.execute(Task(textId, title, Task.TYPE_ITEM_TEXT, text))
    }

    fun attachView(view: TextActInterface) {
        this.view = WeakReference(view)
    }

    fun closeView() {
        view.get()?.saveText(savedIntent)
        view.get()?.finish()
    }

    override fun update(translatedText: Task) {
        try {
            view.get()?.setText(translatedText.title, translatedText.data)
            view.get()?.showToast(R.string.translate_completed)
        }catch (ex: Exception){
            view.get()?.showToast(R.string.translate_fail)
        }
        //view.get()?.setText(title, text)
        //TranslateInteractor.removeObserver()
        Translate.getTranslateObj()!!.removeObserver()
        Translate.delTranslateObj()
    }

    /*@SuppressLint("StaticFieldLeak")
    internal inner class AsyncRequest : AsyncTask<TextActData, Void, Array<String?>?>() {

        override fun doInBackground(vararg params: TextActData): Array<String?>? = try {
            val translatedTitle = YandexTranslate.translate("ru-en",
                    params[0].taskTitle)
            val translatedText = YandexTranslate.translate("ru-en",
                    params[0].taskText)
            arrayOf(translatedTitle, translatedText)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        override fun onPostExecute(strings: Array<String?>?) {
            super.onPostExecute(strings)
                if (strings != null) {
                    view.get()?.setText(strings)
                    view.get()?.showToast(R.string.translate_completed)
                } else {
                    view.get()?.showToast(R.string.translate_fail)
                }
        }
    }*/
}