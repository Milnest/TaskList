package com.milnest.tasklist.presentation.listScreen

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.milnest.tasklist.IntentData
import com.milnest.tasklist.data.repository.DBRepository
import com.milnest.tasklist.entities.CheckboxTaskListItem
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.JsonAdapter
import java.lang.ref.WeakReference
import java.util.*

class ListActivityPresenter {

    private var listId : Int? = null
    private lateinit var view: WeakReference<ListActInterface>
    private var task = Task(Task.TYPE_ITEM_LIST)

    fun attachView(view: ListActInterface) {
        this.view = WeakReference(view)
    }

    fun saveClicked() {
        saveList()
    }

    private fun saveList() {
        val itemList = ArrayList<CheckboxTaskListItem>()
        for (cb in view.get()?.mCheckBoxList!!) {
                itemList.add(CheckboxTaskListItem((cb.second as EditText).text.toString(),
                        (cb.first as CheckBox).isChecked))
        }
        task.data= JsonAdapter.toJson(itemList)
        DBRepository.saveTask(task)
    }

    fun setStartList(extras : Bundle?){
        if (extras != null) {
            listId = extras.getInt(IntentData.ID)
            task = DBRepository.getTaskById(listId!!)!!
            view.get()?.fillStart(JsonAdapter.fromJson(task.data))
        } else {
            view.get()?.addCb(false, "")
        }
    }

    fun addNewCheckBox() = View.OnClickListener {
        view.get()?.addCb(false, "")
    }
}