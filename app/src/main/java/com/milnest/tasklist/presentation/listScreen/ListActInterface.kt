package com.milnest.tasklist.presentation.listScreen

import android.util.Pair
import com.milnest.tasklist.entities.CheckboxTaskListItem

interface ListActInterface {
    var mCheckBoxList: MutableList<Pair<*, *>>?
    fun fillStart(cbList: List<CheckboxTaskListItem>)
    fun addCb(cbState: Boolean, cbText:String?)
}