package com.milnest.tasklist.presentation.listScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.milnest.tasklist.R
import com.milnest.tasklist.application.IntentData
import com.milnest.tasklist.entities.CheckboxTaskListItem
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.other.utils.JsonAdapter
import com.milnest.tasklist.presentation.mainScreen.MainActivity
import com.milnest.tasklist.repository.DBRepository
import java.util.ArrayList

class ListActivityPresenter {

    var listId : Int? = null
    var view: ListActInterface? = null
    fun attachView(activity: ListActInterface) {
        view = activity
    }

    fun detachView() {
        view = null
    }


    fun saveClicked() {
        saveList()
    }

    private fun saveList() {
        val data = Intent()
        val taskCbList: ListOfCheckboxesTaskListItem
        if (listId != null) {
            taskCbList = ListOfCheckboxesTaskListItem(
                    listId!!, "", TaskListItem.TYPE_ITEM_LIST,
                    ArrayList())
            data.putExtra(IntentData.ID, listId!!)
        } else {
            taskCbList = ListOfCheckboxesTaskListItem(
                    0, "", TaskListItem.TYPE_ITEM_LIST,
                    ArrayList())
        }

        val itemList = ArrayList<CheckboxTaskListItem>()
        for (cb in view!!.mCheckBoxList!!) {
            if (taskCbList.cbList != null) {
                itemList.add(CheckboxTaskListItem((cb.second as EditText).text.toString(),
                        (cb.first as CheckBox).isChecked))

            }
        }
        taskCbList.cbList = itemList

        //id просто игнорируется при добавлении нового активити
        data.putExtra(IntentData.LIST, JsonAdapter.toJson(taskCbList))
        view!!.recieveList(data)
    }

    fun setStartList(extras : Bundle?){
        if (extras != null) {
            listId = extras.getInt(IntentData.ID)
            val cbList = DBRepository.getTaskById(listId!!)

            view!!.fillStart(cbList as ListOfCheckboxesTaskListItem)
        } else {
            view!!.firstFill()
        }
    }

    fun addNewCheckBox() = View.OnClickListener {
        view!!.newCb()
    }
}