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
    private lateinit var taskPresenter: ListTaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)
        bindViews()
    }

    private fun bindViews() {
        setSupportActionBar(toolbar)
        initPresenter()
//        new_cb.setOnClickListener(taskPresenter.addNewCheckBox())
    }

    private fun initPresenter() {
        taskPresenter = ListTaskPresenter()
        taskPresenter.attachView(this)
        recycler_view_cb.layoutManager = LinearLayoutManager(this)
        taskPresenter.setAdapter(recycler_view_cb)
        taskPresenter.setStartList(intent.extras)
        /*recycler_view_cb.layoutManager = LinearLayoutManager(this)*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        taskPresenter.saveClicked()
        return true
    }
}
