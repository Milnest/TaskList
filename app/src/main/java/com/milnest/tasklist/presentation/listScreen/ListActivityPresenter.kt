package com.milnest.tasklist.presentation.listScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.milnest.tasklist.application.IntentData
import com.milnest.tasklist.entities.CheckboxTaskListItem
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.other.utils.JsonAdapter
import com.milnest.tasklist.repository.DBRepository
import java.lang.ref.WeakReference
import java.util.ArrayList

class ListActivityPresenter {

    private var listId : Int? = null
    private lateinit var view: WeakReference<ListActInterface>

    fun attachView(view: ListActInterface) {
        this.view = WeakReference(view)
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
        for (cb in view.get()?.mCheckBoxList!!) {
            if (taskCbList.cbList != null) {
                itemList.add(CheckboxTaskListItem((cb.second as EditText).text.toString(),
                        (cb.first as CheckBox).isChecked))
            }
        }
        taskCbList.cbList = itemList

        //id просто игнорируется при добавлении нового активити
        data.putExtra(IntentData.LIST, JsonAdapter.toJson(taskCbList))
        view.get()?.recieveList(data)
    }

    fun setStartList(extras : Bundle?){
        if (extras != null) {
            listId = extras.getInt(IntentData.ID)
            val cbList = DBRepository.getTaskById(listId!!)

            view.get()?.fillStart(cbList as ListOfCheckboxesTaskListItem)
        } else {
            view.get()?.addCb(false, "")
        }
    }

    fun addNewCheckBox() = View.OnClickListener {
        view.get()?.addCb(false, "")
    }
}