package com.milnest.tasklist.use_cases

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.util.Base64
import com.milnest.tasklist.*
import com.milnest.tasklist.presenter.Presenter
import com.milnest.tasklist.repository.DBAdapter

/**
 * Created by t-yar on 22.04.2018.
 */

/**Класс для обобщения работы с базой данных
 */
class DBMethodsAdapter private constructor(var taskList : MutableList<TaskListItem>,
                                           val db: DBAdapter, val activity: MainActivity) {

    fun open(){
        db.openDB()
    }

    fun close(){
        db.close()
    }

    fun retrieve() {

        taskList.clear()

        //SELECT
        val c = db.allTasks

        //DATA ADDING TO ARRAYLIST
        showItems(c)

    }

    /**Отображает список задач, согласно текущим условиям(в том числе и для поиска)
     */
    private fun showItems(c: Cursor) {
//        taskAdapter.notifyDataSetChanged()
        activity.adapter.notifyDataSetChanged()
        //вместо верхней строчки обращение к презентеру

        while (c.moveToNext()) {
            val id = c.getInt(0)
            val name = c.getString(1)
            val type = c.getInt(2)
            val content = c.getString(3)

            when (type) {
                ItemsAdapter.TYPE_ITEM_TEXT -> taskList.add(TextTaskListItem(id, name, content))
                ItemsAdapter.TYPE_ITEM_IMAGE -> {
                    val bytes = Base64.decode(content, Base64.DEFAULT)
                    taskList.add(ImgTaskListItem(id, name,
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)))
                }
                ItemsAdapter.TYPE_ITEM_LIST -> {
                    val cbList = JsonAdapter.fromJson(content)
                    cbList.id = id
                    taskList.add(cbList)
                }
            }
        }
    }


    /**Сохраняет новую задачу
     */
    fun save(name: String, type: Int, content: String) {

        //INSERT
        val result = db.add(name, type, content)

        if (result > 0) {
//            Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show()
        } else {
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
                Presenter(activity).toastToActivity(activity.getString(R.string.delete_OK))
//            Toast.makeText(this, "Задача успешно удалена!", Toast.LENGTH_SHORT).show()
            } else {
                Presenter(activity).toastToActivity(activity.getString(R.string.delete_canceled))
//            Toast.makeText(this, "Ошибка удаления!", Toast.LENGTH_SHORT).show()
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
//            Toast.makeText(this, "Задача изменена!", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(this, "Ошибка изменения!", Toast.LENGTH_SHORT).show()
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
        private var dbMethodsAdapter: DBMethodsAdapter? = null
        fun setDBMethodsAdapter(activity: MainActivity){
            if(dbMethodsAdapter == null) {
                dbMethodsAdapter = DBMethodsAdapter(MainActivity.mTaskListItems,
                        DBAdapter.getDBAdapter()!!, activity)
            }
        }
        fun getDBMethodsAdapter() = dbMethodsAdapter!!
    }
}
