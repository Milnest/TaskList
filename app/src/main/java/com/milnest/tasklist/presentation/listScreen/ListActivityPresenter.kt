package com.milnest.tasklist.presentation.listScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.milnest.tasklist.IntentData
import com.milnest.tasklist.entities.CheckboxTaskListItem
import com.milnest.tasklist.other.utils.JsonAdapter
import com.milnest.tasklist.data.repository.DBRepository
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
        //val taskCbList: ListOfCheckboxesTaskListItem
        val itemList = ArrayList<CheckboxTaskListItem>()
        for (cb in view.get()?.mCheckBoxList!!) {
                itemList.add(CheckboxTaskListItem((cb.second as EditText).text.toString(),
                        (cb.first as CheckBox).isChecked))
        }
        val jsonStringOfList = JsonAdapter.toJson(itemList)

        if (listId != null) data.putExtra(IntentData.ID, listId!!)

        //id просто игнорируется при добавлении нового активити
        data.putExtra(IntentData.LIST, jsonStringOfList)
        view.get()?.recieveList(data)
    }

    fun setStartList(extras : Bundle?){
        if (extras != null) {
            listId = extras.getInt(IntentData.ID)
            //val cbList = DBRepository.getTaskById(listId!!)!!.data
            val cbList = JsonAdapter.fromJson(DBRepository.getTaskById(listId!!)!!.data)

            view.get()?.fillStart(cbList as List<CheckboxTaskListItem>)
        } else {
            view.get()?.addCb(false, "")
        }
    }

    fun addNewCheckBox() = View.OnClickListener {
        view.get()?.addCb(false, "")
    }
}