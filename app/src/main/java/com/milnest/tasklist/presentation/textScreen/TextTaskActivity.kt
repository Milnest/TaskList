package com.milnest.tasklist.presentation.textScreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.TextActData
import kotlinx.android.synthetic.main.activity_text_task.*
import kotlinx.android.synthetic.main.toolbar.*

/**Класс текстовой задачи
 */
class TextTaskActivity : AppCompatActivity(), TextActInterface {
    private lateinit var presenter : TextActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_task)
        bindViews()
    }

    private fun bindViews() {
        presenter = TextActivityPresenter()
        presenter.attachView(this)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        presenter.setStartText(intent.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_text, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val textActData = TextActData(taskTitle.text.toString(), taskText.text.toString())
        when (item.itemId) {
            R.id.action_task_text_save -> {
                presenter.saveClicked(textActData)
                //saveText()
                presenter.closeView()
            }
            R.id.action_task_text_share -> {
                //testitb
                //presenter.saveButtonClicked()
                presenter.shareClicked(textActData)
            }
            R.id.action_task_text_translate -> presenter.translationClicked(textActData)
        }
        return true
    }

    override fun startShareAct(shareIntent: Intent){
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    override fun saveText(data : Intent) {
        setResult(Activity.RESULT_OK, data)
    }

    override fun setText(title: String, text: String){
        taskTitle.setText(title)
        taskText.setText(text)
    }

    override fun showToast(toShow: Int) {
        Toast.makeText(this, getString(toShow),
                Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED, Intent())
    }
}
