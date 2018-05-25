package com.milnest.tasklist.presentation.list

import android.content.Intent
import android.util.Pair
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem

interface ListActInterface {
    var mCheckBoxList: MutableList<Pair<*, *>>?
    //fun getText() : TextActData
    /*fun saveText(data : Intent)
    fun setText(strings: Array<String>, id: Int?)*/
    fun getStartText() : Intent?

    fun fillStart(cbList: ListOfCheckboxesTaskListItem)
    /*fun showToast(toShow : Int)*/
    fun firstFill()
    fun recieveList(data: Intent)
    fun newCb()
}