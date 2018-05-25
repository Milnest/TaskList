package com.milnest.tasklist.presentation.list

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.CheckboxTaskListItem
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.interactor.JsonAdapter
import com.milnest.tasklist.presentation.main.MainActivity
import java.util.ArrayList

class ListActivityPresenter : View.OnClickListener {

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
            data.putExtra(MainActivity.ID, listId!!)
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
        data.putExtra(MainActivity.LIST, JsonAdapter.toJson(taskCbList))
        view!!.recieveList(data)
    }

    fun startFillUsed(){
        setStartList()
    }

    private fun setStartList(){
        val extras = view!!.getStartText()!!.extras
        if (extras != null) {
            val data = extras.getStringArray("data")
            val listData = data!![1]
            val cbList = JsonAdapter.fromJson(listData.toString()) //toString
            listId = extras.getInt("id")

            view!!.fillStart(cbList)
        } else {
            view!!.firstFill()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.new_cb -> {
                view!!.newCb()
            }
        }
    }
}