package com.milnest.tasklist.presentation.listScreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem
import com.milnest.tasklist.other.utils.ChangeCbColor

import java.util.ArrayList

class ListTaskActivity : AppCompatActivity(), ListActInterface {
    private var newCheckbox: TextView? = null
    private var mToolbar: Toolbar? = null
    //Пара значений, чекбокс и его редактируемый текст
    override var mCheckBoxList: MutableList<Pair<*, *>>? = null
    private var addListTaskLayout: LinearLayout? = null
    private var mId: Int? = null
    private var mListData: String? = null
    private var extras: Bundle? = null
    lateinit var presenter: ListActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)
        setInitialData()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setInitialData() {
        newCheckbox = findViewById<View>(R.id.new_cb) as TextView
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        addListTaskLayout = findViewById<View>(R.id.add_list_task_layout) as LinearLayout
        mCheckBoxList = ArrayList()
        extras = intent.extras
        initPresenter()
        newCheckbox!!.setOnClickListener(presenter)
    }

    private fun initPresenter() {
        presenter = ListActivityPresenter()
        presenter.attachView(this)
        presenter.startFillUsed()
    }

    override fun getStartText(): Intent?{
        return intent
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //recieveList()
        presenter.saveClicked()
        finish()
        return true
    }

    override fun newCb() {
        val addedCb = CheckBox(this)
        val addedCbText = EditText(this)
        addCb(addedCb, addedCbText)
    }

    //Добавляет чекбокс с edit text и кнопкой для удаления на вывод пользователю
    private fun addCb(cbToAdd: CheckBox, cbTextToAdd: EditText) {
        ChangeCbColor.change(cbToAdd)
        cbToAdd.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        val innerLayout = LinearLayout(this)
        innerLayout.orientation = LinearLayout.HORIZONTAL
        innerLayout.addView(cbToAdd)
        cbTextToAdd.setHint(R.string.new_text)
        cbTextToAdd.layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f)
        innerLayout.addView(cbTextToAdd)
        val delTextView = TextView(this)
        delTextView.text = "X"
        delTextView.setTextColor(resources.getColor(R.color.lum_red))
        delTextView.layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f)
        val cbAndText = Pair(cbToAdd, cbTextToAdd)
        mCheckBoxList!!.add(cbAndText)
        addListTaskLayout!!.addView(innerLayout)
        innerLayout.addView(delTextView)
        delTextView.setOnClickListener {
            addListTaskLayout!!.removeView(innerLayout)
            mCheckBoxList!!.remove(cbAndText)
        }
    }

    //Заполняет UI данными пришедщими с MainActivity для редактирования
    override fun fillStart(cbList: ListOfCheckboxesTaskListItem) {
        //Добавляет чекбоксы с текстом программно
        for (item in cbList.cbList!!) {
            val cb = CheckBox(this)
            cb.isChecked = item.isCbState
            val cbText = EditText(this)
            cbText.setText(item.cbText)
            addCb(cb, cbText)
        }
    }

    //Первичное заполнение
    override fun firstFill(){
        val startCb = CheckBox(this)
        val addedCbText = EditText(this)
        addCb(startCb, addedCbText)
    }


    override fun recieveList(data: Intent) {
        setResult(Activity.RESULT_OK, data)
    }

}
