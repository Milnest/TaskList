package com.milnest.tasklist

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

import java.util.ArrayList

class ListTaskActivity : AppCompatActivity() {

    private var newCheckbox: TextView? = null
    private var mToolbar: Toolbar? = null
    //Пара значений, чекбокс и его редактируемый текст
    private var mCheckBoxList: MutableList<Pair<*, *>>? = null
    private var addListTaskLayout: LinearLayout? = null
    private var mId: Int? = null
    private var mListData: String? = null
    private var extras: Bundle? = null

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
        if (extras != null) {
            val data = extras!!.getStringArray("data")
            mListData = data!![1]
            val cbList = JsonAdapter.fromJson(mListData.toString()) //toString
            mId = extras!!.getInt("id")
            for (item in cbList.cbList!!) {
                val cb = CheckBox(this)
                cb.isChecked = item.isCbState
                val cbText = EditText(this)
                cbText.setText(item.cbText)
                addCb(cb, cbText)
            }
        } else {
            val startCb = CheckBox(this)
            val addedCbText = EditText(this)
            addCb(startCb, addedCbText)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        saveList()
        finish()
        return true
    }

    fun OnClick(view: View) {
        when (view.id) {
            R.id.new_cb -> {
                val addedCb = CheckBox(this)
                val addedCbText = EditText(this)
                addCb(addedCb, addedCbText)
            }
        }
    }

    private fun addCb(cbToAdd: CheckBox, cbTextToAdd: EditText) {
        ChangeCbColor.change(cbToAdd)
        cbToAdd.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
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


    private fun saveList() {
        val data = Intent()
        val taskCbList: ListOfCheckboxesTaskListItem
        if (mId != null) {
            taskCbList = ListOfCheckboxesTaskListItem(
                    mId!!, "", TaskListItem.TYPE_ITEM_LIST,
                    ArrayList())
            data.putExtra(MainActivity.ID, mId!!)
        } else {
            taskCbList = ListOfCheckboxesTaskListItem(
                    0, "", TaskListItem.TYPE_ITEM_LIST,
                    ArrayList())
        }
        val itemList = ArrayList<CheckboxTaskListItem>()
        for (cb in mCheckBoxList!!) {
            if (taskCbList.cbList != null) {
                itemList.add(CheckboxTaskListItem((cb.second as EditText).text.toString(),
                        (cb.first as CheckBox).isChecked))

            }
        }
        taskCbList.cbList = itemList

        //id просто игнорируется при добавлении нового активити
        data.putExtra(MainActivity.LIST, JsonAdapter.toJson(taskCbList))
        setResult(Activity.RESULT_OK, data)
    }

}
