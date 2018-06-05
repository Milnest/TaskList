package com.milnest.tasklist.presentation.listScreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem
import com.milnest.tasklist.other.utils.ChangeCbColor
import kotlinx.android.synthetic.main.activity_list_task.*
import kotlinx.android.synthetic.main.checkbox_item.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class ListTaskActivity : AppCompatActivity(), ListActInterface {
    //Пара значений, чекбокс и его редактируемый текст
    override var mCheckBoxList: MutableList<Pair<*, *>>? = null //TODO спросить где хранить это, в активити или презентере
    private var extras: Bundle? = null
    private lateinit var presenter: ListActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)
        bindViews()
    }

    private fun bindViews() {
        setSupportActionBar(toolbar)
        mCheckBoxList = ArrayList()
        extras = intent.extras
        initPresenter()
        new_cb.setOnClickListener(presenter.addNewCheckBox())
    }

    private fun initPresenter() {
        presenter = ListActivityPresenter()
        presenter.attachView(this)
        presenter.setStartList(intent.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.saveClicked()
        finish()
        return true
    }

    override fun addCb(cbState: Boolean, cbText:String?) {
        val innerLayout = View.inflate(this, R.layout.checkbox_item, null)
        ChangeCbColor.change(innerLayout.addedCb)
        innerLayout.addedCb.isChecked = cbState
        innerLayout.addedCbText.setText(cbText)
        val cbAndText = Pair(innerLayout.addedCb, innerLayout.addedCbText)
        mCheckBoxList!!.add(cbAndText)
        add_list_task_layout.addView(innerLayout)
        innerLayout.delTextView.setOnClickListener {
            add_list_task_layout.removeView(innerLayout)
            mCheckBoxList!!.remove(cbAndText)
        }
    }

    override fun fillStart(cbList: ListOfCheckboxesTaskListItem) {
        for (item in cbList.cbList!!) {
            addCb(item.isCbState, item.cbText)
        }
    }

    override fun recieveList(data: Intent) {
        setResult(Activity.RESULT_OK, data)
    }

}
