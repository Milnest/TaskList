package com.milnest.tasklist.presentation.textScreen

import android.content.Intent
import android.os.Bundle
import com.milnest.tasklist.R
import com.milnest.tasklist.application.IntentData
import com.milnest.tasklist.entities.TextActData
import com.milnest.tasklist.entities.TextTaskListItem
import com.milnest.tasklist.entities.observer.Observer
import com.milnest.tasklist.interactor.Translate
import com.milnest.tasklist.repository.DBRepository
import java.lang.ref.WeakReference

class TextActivityPresenter : Observer {
    var textId: Int? = null
    private val savedIntent = Intent()
    private lateinit var view: WeakReference<TextActInterface>

    fun setStartText(extras: Bundle?) {
        if (extras != null){
            textId = extras.getInt(IntentData.ID)
            val textTask = DBRepository.getTaskById(textId!!) as TextTaskListItem
            view.get()?.setText(textTask.name, textTask.text)
        }
    }

    fun saveClicked(text: TextActData) {
        savedIntent.putExtra(IntentData.NAME, text.taskTitle)
        savedIntent.putExtra(IntentData.TEXT, text.taskText)

        if (textId != null) {
            savedIntent.putExtra(IntentData.ID, textId!!)
        }
    }

    fun shareClicked(text: TextActData) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text.taskTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text.taskText)
        view.get()?.startShareAct(shareIntent)
    }

    fun translationClicked(textActData: TextActData) {
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
        translate.execute(textActData)
    }

    fun attachView(view: TextActInterface) {
        this.view = WeakReference(view)
    }

    fun closeView() {
        view.get()?.saveText(savedIntent)
        view.get()?.finish()
    }

    override fun update(translatedText: TextActData?) {
        if (translatedText != null) {
            view.get()?.setText(translatedText.taskTitle, translatedText.taskText)
            view.get()?.showToast(R.string.translate_completed)
        } else {
            view.get()?.showToast(R.string.translate_fail)
        }
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