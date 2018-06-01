package com.milnest.tasklist.presentation.textScreen

import android.content.Intent
import android.os.AsyncTask
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.TextActData
import com.milnest.tasklist.entities.TextTaskListItem
import com.milnest.tasklist.interactor.YandexTranslate
import com.milnest.tasklist.presentation.mainScreen.MainActivity
import com.milnest.tasklist.repository.DBRepository
import java.io.IOException

class TextActivityPresenter {

    var textId : Int? = null
    /*lateinit var textId : Int*/
    fun startFillUsed(){
        setStartText()
    }

    private fun setStartText(){
        val extras = view!!.getStartText()!!.extras
        if (extras != null) {
            val id = extras.getInt("id")
            textId = id
            val textTask = DBRepository.getTaskById(textId!!) as TextTaskListItem?
            val name = textTask!!.name
            val text = textTask.text
            val data = arrayOf(name, text)
            view!!.setText(data/*, id*/)
        }
    }

    fun saveButtonClicked(){
        doSave()
    }

    private fun doSave() {
        val text = view!!.getText()
        val data = Intent()
        data.putExtra(MainActivity.NAME, text.taskTitle)
        data.putExtra(MainActivity.TEXT, text.taskText)
        /*val id : Int? = text.taskId
        if (id != null) {
            data.putExtra(MainActivity.ID, id)
        }*/
        val id = textId
        if (textId != null) {
            data.putExtra(MainActivity.ID, id)
        }
        view!!.saveText(data)

    }

    fun shareButtonClicked(){
        doShare()
    }

    private fun doShare() {
        val text = view!!.getText()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text.taskTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text.taskText)
        view!!.startShareAct(shareIntent)
    }

    fun translationButtonClicked(){
        doTranslation()
    }

    private fun doTranslation() {
        AsyncRequest().execute(view!!.getText())
    }

    var view: TextActInterface? = null
    fun attachView(activity: TextActInterface) {
        view = activity
    }

    fun detachView() {
        view = null
    }

    internal inner class AsyncRequest : AsyncTask<TextActData, Void, Array<String?>>() {

        override fun doInBackground(vararg params: TextActData): Array<String?>? {
            try {
                val translatedTitle = YandexTranslate.translate("ru-en",
                        params[0].taskTitle)
                val translatedText = YandexTranslate.translate("ru-en",
                        params[0].taskText)
                return arrayOf(translatedTitle, translatedText)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(strings: Array<String?>) {
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