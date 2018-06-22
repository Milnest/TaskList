package com.milnest.tasklist.presentation.list

import com.milnest.tasklist.entities.CheckboxTaskListItem

interface ListTaskView {
    fun fillStart(cbList: List<CheckboxTaskListItem>)
    fun addCb(cbState: Boolean, cbText:String?)
    fun finish()
}