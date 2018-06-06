package com.milnest.tasklist.presentation.listScreen

import android.content.Intent
import android.util.Pair
import com.milnest.tasklist.entities.CheckboxTaskListItem

interface ListActInterface {
    var mCheckBoxList: MutableList<Pair<*, *>>?
    //fun getStartText() : Intent?

    fun fillStart(cbList: List<CheckboxTaskListItem>)
    //fun firstFill()
    fun recieveList(data: Intent)
    fun addCb(cbState: Boolean, cbText:String?)
   // fun newCb()
}