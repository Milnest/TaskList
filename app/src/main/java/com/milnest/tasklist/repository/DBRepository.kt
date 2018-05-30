package com.milnest.tasklist.repository

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.milnest.tasklist.application.app
import com.milnest.tasklist.db.TaskDatabaseHelper
import com.milnest.tasklist.entities.ImgTaskListItem
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.entities.TextTaskListItem
import com.milnest.tasklist.other.utils.JsonAdapter
import com.milnest.tasklist.other.utils.PhotoInteractor

/**
 * Created by t-yar on 21.04.2018.
 */

class DBRepository private constructor() {
    private var db: SQLiteDatabase = TaskDatabaseHelper(app.context).writableDatabase

    fun getAllTasks() : MutableList<TaskListItem>{
        val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)

        val cursor = db.query(TaskDatabaseHelper.TABLE, columns,
                null, null, null, null, null)
        val curList = getCurList(cursor)
        cursor.close()
        return curList
    }

    private fun getCurList(cursor: Cursor): MutableList<TaskListItem> {
        val taskList: MutableList<TaskListItem> = ArrayList()

        val indexId = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_ID)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(indexId)
            val name = cursor.getString(1)
            val type = cursor.getInt(2)
            val content = cursor.getString(3)

            when (type) {
                TaskListItem.TYPE_ITEM_TEXT -> taskList.add(TextTaskListItem(id, name, content))
                TaskListItem.TYPE_ITEM_IMAGE -> {
                    taskList.add(ImgTaskListItem(id, name, PhotoInteractor.createImage(content)))
                }
                TaskListItem.TYPE_ITEM_LIST -> {
                    val cbList = JsonAdapter.fromJson(content)
                    cbList.id = id
                    taskList.add(cbList)
                }
            }
        }
        cursor.close()
        return taskList
    }

    fun add(name: String, type: Int, content: String):MutableList<TaskListItem>{
        try {
            val cv = ContentValues()
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name)
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type)
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content)
            db.insert(TaskDatabaseHelper.TABLE, TaskDatabaseHelper.COLUMN_ID, cv)
        }
        catch (e: SQLException) {
            }
        return getAllTasks()
    }

    fun getTaskById(id: Int): Array<String> {
/*        val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)
        val cursor = db.query(TaskDatabaseHelper.TABLE, columns,
                TaskDatabaseHelper.COLUMN_ID + "=" + id, null, null, null, null)*/
        val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)

        val cursor = db.query(TaskDatabaseHelper.TABLE, columns,
                null, null, null, null, null)

        var name = ""
        var content = ""
        while (cursor.moveToNext()) {
            val get_id = cursor.getInt(0)
            //TODO перекодить, забирая значения из БД напрямую!
            if (get_id == id) {
                name = cursor.getString(1)
                //int type = context.getInt(2);
                content = cursor.getString(3)
            }
        }
        cursor.close()
        return arrayOf(name, content)
    }

    fun update(id: Int, name: String, type: Int, content: String) : MutableList<TaskListItem>{
        try {
            val cv = ContentValues()
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name)
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type)
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content)

            //TODO поиск
            /*db.execSQL("update fts_task_table SET name = $name, type = $type, " +
                    "content = $content WHERE _id = $id")*/

            db.update(TaskDatabaseHelper.TABLE, cv,
                    TaskDatabaseHelper.COLUMN_ID + " =?",
                    arrayOf(id.toString())).toLong()
            //notifyObservers(R.string.task_changed)
        } catch (e: SQLException) {
            //TODO
            //notifyObservers(R.string.task_chanceled)
        }
        return getAllTasks()
    }

    fun delete(id: Int) : MutableList<TaskListItem>{
        val del: Int
        try {
            //Исправил
            //db.execSQL("DELETE FROM fts_task_table WHERE _id = $id")
            db.delete(TaskDatabaseHelper.TABLE, TaskDatabaseHelper.COLUMN_ID +
                    " =?", arrayOf(id.toString()))
            //notifyObservers(R.string.delete_OK)
        } catch (e: SQLException) {
            //notifyObservers(R.string.delete_canceled)
            e.printStackTrace()
        }
        return getAllTasks()
    }

    fun search(data: String): MutableList<TaskListItem> {
        val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)
        return getCurList(db.rawQuery("select * from task_table where name = ? OR content = ?",
                arrayOf(data, data)))
    }

    fun searchDynamic(data: String) : MutableList<TaskListItem>{
        //TODO: MATCH
        /*String[] columns={TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT};
        val selectionArgs = arrayOf(data)*/
        /*val cursor = db.rawQuery("SELECT * FROM fts_task_table " +
                "WHERE fts_task_table MATCH ?", selectionArgs)*/
        val cursor = db.rawQuery("SELECT * FROM task_table " +
                "WHERE content OR name LIKE '%$data%'", null)
        return getCurList(cursor)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private val dbRepository: DBRepository = DBRepository()
        fun getDBRepository() = dbRepository
    }

}

