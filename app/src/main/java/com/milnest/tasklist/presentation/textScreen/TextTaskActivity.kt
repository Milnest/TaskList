package com.milnest.tasklist.presentation.textScreen

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.TextActData
import com.milnest.tasklist.interactor.YandexTranslate
import kotlinx.android.synthetic.main.activity_text_task.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException

/**Класс текстовой задачи
 */
class TextTaskActivity : AppCompatActivity(), TextActInterface {
    private var title: String? = null
    private var text: String? = null
    private val TAG = "TextTaskActivity"
    private var mId: Int? = null
    lateinit var presenter : TextActivityPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_task)
        bindViews()
    }

    override fun onResume() {
        super.onResume()
        presenter.startFillUsed()
    }

    private fun bindViews() {
        presenter = TextActivityPresenter()
        presenter.attachView(this)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_text, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_task_text_save -> {
                presenter.saveButtonClicked()
                //saveText()
                finish()
            }
            R.id.action_task_text_share -> {
                presenter.saveButtonClicked()
                presenter.shareButtonClicked()
            }
            R.id.action_task_text_translate -> presenter.translationButtonClicked()
        }
        return true
    }

    override fun startShareAct(shareIntent: Intent){
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    override fun saveText(data : Intent) {
        setResult(Activity.RESULT_OK, data)
    }

    override fun getText(): TextActData {
        title = taskTitle.text.toString()
        text = taskText.text.toString()
        return TextActData(title.toString(), text.toString())
    }

    override fun setText(strings: Array<String?>/*, id : Int?*/){
        //TODO: Вынести Id В презентер
        taskTitle.setText(strings[0])
        taskText.setText(strings[1])
    }

    override fun showToast(toShow: Int) {
        Toast.makeText(this, getString(toShow),
                Toast.LENGTH_SHORT).show()
    }

    override fun getStartText(): Intent?{
        return intent
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED, Intent())
    }

    internal inner class AsyncRequest : AsyncTask<Void, Void, Array<String>>() {

        override fun doInBackground(vararg voids: Void): Array<String>? {
            try {
                val translatedTitle = YandexTranslate.translate("ru-en", title)
                val translatedText = YandexTranslate.translate("ru-en", text)
                return arrayOf(translatedTitle, translatedText)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(strings: Array<String>?) {
            super.onPostExecute(strings)
            if (strings != null) {
                taskTitle.setText(strings[0])
                taskText.setText(strings[1])
                Toast.makeText(this@TextTaskActivity, "Перевод завершён!",
                        Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@TextTaskActivity, "Ошибка перевода!",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }


}
