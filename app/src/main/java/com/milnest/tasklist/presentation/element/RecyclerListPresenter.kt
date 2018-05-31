package com.milnest.tasklist.presentation.element

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuItem
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.presentation.listScreen.ListTaskActivity
import com.milnest.tasklist.presentation.mainScreen.MainActivity
import com.milnest.tasklist.presentation.textScreen.TextTaskActivity
import com.milnest.tasklist.repository.DBRepository

class RecyclerListPresenter: ItemsAdapter.IClickListener,
        android.support.v7.view.ActionMode.Callback{

    var curPosDelete : Int? = null

    override fun onItemClick(position: Int) {
        val id = adapter!!.getItemId(position).toInt()
        val type = adapter!!.getItemViewType(position)
        when (type) {
            TaskListItem.TYPE_ITEM_TEXT -> {
                view!!.startTaskActivity(TextTaskActivity::class.java as? Class<*>,
                        id, MainActivity.TEXT_RESULT, taskRepo.getTaskById(id))
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                view!!.startTaskActivity(ListTaskActivity::class.java,
                        id, MainActivity.LIST_RESULT, taskRepo.getTaskById(id))
            }
        }
    }

    override fun onItemLongClick(position: Int): Boolean {
        if (view!!.mActionMode == null) {
            curPosDelete = position
            view!!.showActionBar(R.string.action_mode)
            //Добавление выделения при выборе
            adapter!!.addSelection(position)
        } else {
            curPosDelete = null
            view!!.closeActionBar()
            //Сброс выделения
            adapter!!.removeSelection()
        }
        return true
    }

    override fun onCreateActionMode(mode: android.support.v7.view.ActionMode, menu: Menu):
            Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.menu_context_task, menu)
        return true
    }

    override fun onPrepareActionMode(mode: android.support.v7.view.ActionMode, menu: Menu):
            Boolean {
        return false
    }

    override fun onActionItemClicked(mode: android.support.v7.view.ActionMode,
                                     item: MenuItem): Boolean {
        if (curPosDelete is Int) {
            taskRepo.deleteTask(adapter!!.getItemId(curPosDelete!!).toInt())
            adapter!!.setData(taskRepo.getAllTasks())
            view!!.mActionMode!!.finish()
        }
        return false
    }

    override fun onDestroyActionMode(mode: android.support.v7.view.ActionMode) {
        adapter!!.removeSelection()
        view!!.mActionMode = null
    }

    companion object{
        var view: ActModeInterface? = null
        var adapter: ItemsAdapter? = null

        //private val dataInteractor = TaskDataInteractor.getDBMethodsAdapter()

        @SuppressLint("StaticFieldLeak")
        private val taskRepo = DBRepository.getDBRepository()

        fun attachView(activity: ActModeInterface){
            view = activity
        }

        fun detachView() {
            view = null
        }

        fun attachAdapter(itemsAdapter: ItemsAdapter){
            adapter = itemsAdapter
        }

        fun detachAdapter() {
            adapter = null
        }
    }
}