package com.milnest.tasklist.presentation.element

import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ObserverInterfaces.Observer
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.interactor.TaskDataInteractor
import com.milnest.tasklist.presentation.list.ListTaskActivity
import com.milnest.tasklist.presentation.main.MainActivity
import com.milnest.tasklist.presentation.text.TextTaskActivity

class RecyclerHolderPresenter(var position: Int, var type: Int) : View.OnLongClickListener,
        View.OnClickListener{

    /*override fun update(notifObject: Any?) {
        adapter!!.retrieveAdapter()
    }*/

    override fun onLongClick(v: View): Boolean {
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

    override fun onClick(v: View) {
        when (type) {
            TaskListItem.TYPE_ITEM_TEXT -> {
                view!!.startTaskActivity(TextTaskActivity::class.java as? Class<*>,
                        /*MainActivity.mTaskListItems[position].id*/
                        dataInteractor.taskList[position].id,
                        MainActivity.TEXT_RESULT)
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                view!!.startTaskActivity(ListTaskActivity::class.java,
                        dataInteractor.taskList[position].id,
                        MainActivity.LIST_RESULT)
            }
        }
    }


    companion object : Observer, android.support.v7.view.ActionMode.Callback{
        var view: ActModeInterface? = null
        var adapter: ItemsAdapterInterface? = null
        var curPosDelete : Int? = null

        private val dataInteractor = TaskDataInteractor.getDBMethodsAdapter()

        fun attachView(activity: ActModeInterface){
            view = activity
        }

        fun detachView() {
            view = null
        }

        fun attachAdapter(itemsAdapterInterface: ItemsAdapterInterface){
            adapter = itemsAdapterInterface
        }

        fun detachAdapter() {
            adapter = null
        }

        override fun update(notifObject: Any?) {
            adapter!!.retrieveAdapter()
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
                dataInteractor.delete(dataInteractor.taskList[curPosDelete!!].id)
                //adapter!!.removeItem(dataInteractor.taskList[curPosDelete!!].id)
                view!!.mActionMode!!.finish()
            }
            return false
        }

        override fun onDestroyActionMode(mode: android.support.v7.view.ActionMode) {
            adapter!!.removeSelection()
            view!!.mActionMode = null
        }

    }
}