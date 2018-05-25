package com.milnest.tasklist.interactor

import android.database.Cursor
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ImgTaskListItem
import com.milnest.tasklist.entities.ObserverInterfaces.Observable
import com.milnest.tasklist.entities.ObserverInterfaces.Observer
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.entities.TextTaskListItem
import com.milnest.tasklist.presentation.element.ItemsAdapter
import com.milnest.tasklist.repository.DBRepository

/**
 * Created by t-yar on 22.04.2018.
 */

class TaskDataInteractor private constructor() :
        Observable {

    private var observersList : MutableList<Observer> = ArrayList()

    val db: DBRepository = DBRepository.getDBRepository()
    var taskList : MutableList<TaskListItem> = ArrayList()

    override fun registerObserver(o: Observer) {
        observersList.add(o)
    }

    override fun removeObserver(o: Observer) {
        observersList.remove(o)
        //observer = null
    }

    override fun notifyObservers(notifObject : Any?) {
        for (o in observersList) o.update(notifObject)
    }

    fun retrieve() {
        taskList.clear()
        val c = db.allTasks
        showItems(c)
    }

    private fun showItems(c: Cursor) {
        notifyObservers(true)

        while (c.moveToNext()) {
            val id = c.getInt(0)
            val name = c.getString(1)
            val type = c.getInt(2)
            val content = c.getString(3)

            when (type) {
                ItemsAdapter.TYPE_ITEM_TEXT -> taskList.add(TextTaskListItem(id, name, content))
                ItemsAdapter.TYPE_ITEM_IMAGE -> {
                    //taskList.add(ImgTaskListItem(id, name, BitmapFactory.decodeFile(content)))
                    taskList.add(ImgTaskListItem(id, name, PhotoInteractor.createImage(content)))
                    /*val bytes = Base64.decode(content, Base64.DEFAULT)
                    taskList.add(ImgTaskListItem(id, name,
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)))*/
                }
                ItemsAdapter.TYPE_ITEM_LIST -> {
                    val cbList = JsonAdapter.fromJson(content)
                    cbList.id = id
                    taskList.add(cbList)
                }
            }
        }
        //activity.adapter.notifyDataSetChanged()
    }


    /**Сохраняет новую задачу
     */
    fun save(name: String, type: Int, content: String) {

        //INSERT
        val result = db.add(name, type, content)

        if (result > 0) {
            notifyObservers(R.string.save_OK)
//            Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show()
        } else {
            notifyObservers(R.string.save_canceled)
//            Toast.makeText(this, "Ошибка добавления!", Toast.LENGTH_SHORT).show()
        }

        //refresh
        retrieve()

    }

    /**Удаляет значение по id
     */
    fun delete(id: Int) {

        //DELETE
        val result = db.Delete(id)

            if (result > 0) {
                notifyObservers(R.string.delete_OK)
            } else {
                notifyObservers(R.string.delete_canceled)
            }

        //refresh
        retrieve()

    }

    /**Выполняет получение значения по id
     */
    fun getById(id: Int): Array<String> {

        //SELECT
        val c = db.allTasks
        var name = ""
        var content = ""

        //LOOP THRU THE DATA ADDING TO ARRAYLIST
        while (c.moveToNext()) {
            val get_id = c.getInt(0)
            //TODO перекодить, забирая значения из БД напрямую!
            if (get_id == id) {
                name = c.getString(1)
                //int type = c.getInt(2);
                content = c.getString(3)
            }
        }

        return arrayOf(name, content)
    }

    /**Осуществляет редактирование значения по id
     */
    fun edit(id: Int, name: String, type: Int, content: String) {

        //INSERT
        val result = db.UPDATE(id, name, type, content)

        if (result > 0) {
            notifyObservers(R.string.task_changed)
        } else {
            notifyObservers(R.string.task_chanceled)
        }

        //refresh
        retrieve()

    }

    /**Осуществляет простой поиск из строки прямым сравнением значений
     */
    fun search(textToSearch: String?) {
        taskList.clear()
        val c = db.Search(textToSearch!!)
        showItems(c)
    }

    /**Осуществляет динамический поиск из строки
     */
    fun searchDynamic(textToSearch: String) {
        taskList.clear()
        val c = db.SearchDynamic(textToSearch)
        showItems(c)
    }

    companion object {
        private var taskDataInteractor: TaskDataInteractor? = null
        fun getDBMethodsAdapter(): TaskDataInteractor{
            if(taskDataInteractor == null) {
                taskDataInteractor = TaskDataInteractor()
            }
            return taskDataInteractor!!
        }
    }
}
