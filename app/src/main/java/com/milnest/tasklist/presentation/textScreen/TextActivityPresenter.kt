package com.milnest.tasklist.presentation.textScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.NotificationCompatExtras
import com.milnest.tasklist.R
import com.milnest.tasklist.application.IntentData
import com.milnest.tasklist.entities.TextActData
import com.milnest.tasklist.entities.TextTaskListItem
import com.milnest.tasklist.interactor.YandexTranslate
import com.milnest.tasklist.repository.DBRepository
import java.io.IOException

class TextActivityPresenter {
    var textId: Int? = null
    private val savedIntent = Intent()
    var view: TextActInterface? = null

    fun setStartText(extras: Bundle?) {
        if (extras != null){
            textId = extras.getInt(/*"id"*/IntentData.ID)
            val textTask = DBRepository.getTaskById(textId!!) as TextTaskListItem?
            val data = arrayOf(textTask!!.name, textTask.text)
            view!!.setText(data)
        }
    }

    fun doSave(text: TextActData) {
        savedIntent.putExtra(IntentData.NAME, text.taskTitle)
        savedIntent.putExtra(IntentData.TEXT, text.taskText)

        if (textId != null) {
            savedIntent.putExtra(IntentData.ID, textId!!)
        }
        /*view!!.saveText(data)*/
    }

    fun doShare(text: TextActData) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text.taskTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text.taskText)
        view!!.startShareAct(shareIntent)
    }

    fun doTranslation(textActData: TextActData) {
        AsyncRequest().execute(textActData)
    }

    fun attachView(activity: TextActInterface) {
        view = activity
    }

    fun detachView() {
        view = null
    }

    fun closeView() {
        view!!.saveText(savedIntent)
        view!!.finishView()
    }

    @SuppressLint("StaticFieldLeak")
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
                view!!.setText(strings/*, null*/)
                view!!.showToast(R.string.translate_completed)
            } else {
                view!!.showToast(R.string.translate_fail)
            }
        }
    }
}