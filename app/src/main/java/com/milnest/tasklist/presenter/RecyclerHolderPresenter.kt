package com.milnest.tasklist.presenter

import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.view.ListTaskActivity
import com.milnest.tasklist.view.MainActivity
import com.milnest.tasklist.view.TextTaskActivity

class RecyclerHolderPresenter(var position: Int, var type: Int) : View.OnLongClickListener,
        View.OnClickListener{

    override fun onLongClick(v: View): Boolean {
        if (view!!.mActionMode == null) {
            view!!.showActionBar(R.string.action_mode)
            //Добавление выделения при выборе
            adapter!!.addSelection(position)
        } else {
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
                        MainActivity.mTaskListItems[position].id,
                        MainActivity.TEXT_RESULT)
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                view!!.startTaskActivity(ListTaskActivity::class.java,
                        MainActivity.mTaskListItems[position].id,
                        MainActivity.LIST_RESULT)
            }
        }
    }


    companion object {
        var view: ActModeInterface? = null
        var adapter: ItemsAdapterInterface? = null

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
    }
}