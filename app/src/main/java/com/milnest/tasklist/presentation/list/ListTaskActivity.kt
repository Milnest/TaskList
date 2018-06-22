package com.milnest.tasklist.presentation.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.CheckboxTaskListItem
import kotlinx.android.synthetic.main.activity_list_task.*
import kotlinx.android.synthetic.main.checkbox_item.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class ListTaskActivity : AppCompatActivity(), ListTaskView {
    //Пара значений, чекбокс и его редактируемый текст
 //   var checkBoxList: MutableList<Pair<*, *>>? = null //TODO: превратить это в листвью
    private lateinit var taskPresenter: ListTaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)
        bindViews()
    }

    private fun bindViews() {
        setSupportActionBar(toolbar)

        //checkBoxList = ArrayList()
        initPresenter()
        new_cb.setOnClickListener(taskPresenter.addNewCheckBox())
    }

    private fun initPresenter() {
        taskPresenter = ListTaskPresenter()
        taskPresenter.attachView(this)
        taskPresenter.setStartList(intent.extras)
        taskPresenter.setAdapter(recycler_view_cb)
        recycler_view_cb.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        taskPresenter.saveClicked(/*checkBoxList*/)
        return true
    }

    override fun addCb(cbState: Boolean, cbText:String?) {
        /*val innerLayout = View.inflate(this, R.layout.checkbox_item, null)
        innerLayout.addedCb.isChecked = cbState
        innerLayout.addedCbText.setText(cbText)
        val cbAndText = Pair(innerLayout.addedCb, innerLayout.addedCbText)
        checkBoxList!!.add(cbAndText)
        add_list_task_layout.addView(innerLayout)
        innerLayout.delTextView.setOnClickListener {
            add_list_task_layout.removeView(innerLayout)
            checkBoxList!!.remove(cbAndText)
        }*/
    }

    override fun fillStart(cbList: List<CheckboxTaskListItem>) {
        for (item in cbList) {
            addCb(item.isCbState, item.cbText)
        }
    }
}
