package com.milnest.tasklist.presentation.element

import android.support.v7.widget.RecyclerView
import com.milnest.tasklist.entities.TaskListItem

interface ItemsAdapterInterface {
    fun removeSelection()
    fun addSelection(position:Int)
    fun retrieveAdapter()
    fun setData(data: List<TaskListItem>)
}