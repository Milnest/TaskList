package com.milnest.tasklist.presentation.list

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import com.milnest.tasklist.App
import com.milnest.tasklist.ID
import com.milnest.tasklist.data.repository.DBRepository
import com.milnest.tasklist.entities.CheckboxTaskListItem
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.JsonAdapter
import java.lang.ref.WeakReference

class ListTaskPresenter {

    val adapter = ListTaskAdapter(onItemClickListener)
    private var listId : Int? = null
    private lateinit var taskView: WeakReference<ListTaskView>
    private var task = Task(Task.TYPE_ITEM_LIST)

    fun attachView(taskView: ListTaskView) {
        this.taskView = WeakReference(taskView)
    }

    fun saveClicked(/*checkBoxList : MutableList<Pair<*, *>>?*/) {
        val itemList = ArrayList<CheckboxTaskListItem>()
        /*for (cb in checkBoxList!!) {
                itemList.add(CheckboxTaskListItem((cb.second as EditText).text.toString(),
                        (cb.first as CheckBox).isChecked))
        }*/
        itemList.addAll(adapter.getList())
        task.data= JsonAdapter.toJson(itemList)
        DBRepository.saveTask(task)
        taskView.get()?.finish()
    }

    fun setStartList(extras : Bundle?){
        if (extras != null) {
            listId = extras.getInt(ID)
            task = DBRepository.getTaskById(listId!!)!!
            adapter.setData(JsonAdapter.fromJson(task.data))
            //taskView.get()?.fillStart(JsonAdapter.fromJson(task.data))
        } else {
            //taskView.get()?.addCb(false, "")
//            adapter.addCb(CheckboxTaskListItem("", false))
            val cb = CheckBox(App.context)
            cb.isChecked = false
            adapter.addCb(cb)
        }
    }

    fun addNewCheckBox() = View.OnClickListener {
        //taskView.get()?.addCb(false, "")
//        adapter.addCb(CheckboxTaskListItem("", false))
        val cb = CheckBox(App.context)
        cb.isChecked = false
        adapter.addCb(cb)
    }

    fun setAdapter(recycler_view_cb: RecyclerView) {
        recycler_view_cb.adapter = adapter
    }

    private val onItemClickListener: ListTaskAdapter.CbClickListener
        get() = object : ListTaskAdapter.CbClickListener {
            override fun onStateChanged(layoutPosition: Int, state: Boolean) {
                adapter.changeState(layoutPosition, !state)
            }

            /*override fun onTextChanged(layoutPosition: Int, text: String): TextWatcher {
                adapter.changeText(layoutPosition, text)
            }*/

            override fun onItemClick(position: Int) {
                if (position != -1) {
                    adapter.deleteCb(position)
                }
            }
        }
}