package com.milnest.tasklist.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.Selection.removeSelection
import android.view.SurfaceHolder
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.application.app
import com.milnest.tasklist.view.ItemsAdapter
import com.milnest.tasklist.view.ItemsAdapter.Companion.tempViewHolderPosition
import com.milnest.tasklist.view.ListTaskActivity
import com.milnest.tasklist.view.MainActivity
import com.milnest.tasklist.view.TextTaskActivity

class RecyclerHolderPresenter(var holder: RecyclerView.ViewHolder) : View.OnLongClickListener,
        View.OnClickListener {

    //TODO : активизировать снятие и удаление выделения
    override fun onLongClick(v: View): Boolean {
        if (view!!.mActionMode == null) {
            view!!.showActionBar(R.string.action_mode)
            tempViewHolderPosition = holder.adapterPosition
            //Добавление выделения при выборе
            //addSelection(mViewHolder)
        } else {
            view!!.closeActionBar()
            //Сброс выделения
            //removeSelection()
        }
        /*view.showActionMode*/
        return true
    }

    override fun onClick(v: View) {
        tempViewHolderPosition = holder.adapterPosition
        when (holder.itemViewType) {
            ItemsAdapter.TYPE_ITEM_TEXT -> {
                view!!.startTaskActivity(TextTaskActivity::class.java /*as? Class<*>*/,
                        MainActivity.mTaskListItems[tempViewHolderPosition].id,
                        MainActivity.TEXT_RESULT)
            }
            ItemsAdapter.TYPE_ITEM_LIST -> {
                view!!.startTaskActivity(ListTaskActivity::class.java,
                        /*mItems!!*/MainActivity.mTaskListItems[tempViewHolderPosition].id,
                        MainActivity.LIST_RESULT)
            }
        }
    }

    companion object {
        var view: ActModeInterface? = null

        fun attachView(activity: ActModeInterface){
            view = activity
        }

        fun detachView() {
            view = null
        }
    }
}