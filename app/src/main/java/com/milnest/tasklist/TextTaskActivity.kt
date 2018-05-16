package com.milnest.tasklist

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast

import java.io.IOException

/**Класс текстовой задачи
 */
class TextTaskActivity : AppCompatActivity() {

    internal lateinit var taskTitle: EditText
    internal lateinit var taskText: EditText
    private var title: String? = null
    private var text: String? = null
    internal lateinit var mToolbar: Toolbar
    private val TAG = "TextTaskActivity"
    private var mId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_task)
        setInitialData()
    }

    override fun onResume() {
        super.onResume()
        val extras = intent.extras
        if (extras != null) {
            val data = extras.getStringArray("data")
            taskTitle.setText(data!![0])
            taskText.setText(data[1])
            mId = extras.getInt("id")
        }
    }

    private fun setInitialData() {
        taskTitle = findViewById<View>(R.id.taskTitle) as EditText
        taskText = findViewById<View>(R.id.taskText) as EditText
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_text, menu)
        /*Log.d(TAG, "onCreateOptionsMenu: Вызов инфлейтера");*/
        /*taskTitle.setText("called");*/
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_task_text_save -> {
                saveText()
                finish()
            }
            R.id.action_task_text_share -> {
                saveText()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            }
            R.id.action_task_text_translate -> translation()
        }//finish();
        return true
    }

    /**Запускает метод в другом потоке для выполнения перевода
     */
    private fun translation() {
        getText()
        AsyncRequest().execute()
    }

    /**Сохраняет данные в Intent
     */
    private fun saveText() {
        val data = Intent()
        getText()
        data.putExtra(MainActivity.NAME, title)
        data.putExtra(MainActivity.TEXT, text)
        if (mId != null) {
            data.putExtra(MainActivity.ID, mId!!)
        }
        setResult(Activity.RESULT_OK, data)
    }

    private fun getText() {
        title = taskTitle.text.toString()
        text = taskText.text.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED, Intent())
    }

    /**Выполняет перевод данной текстовой задачи с русского на английский язык
     */
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