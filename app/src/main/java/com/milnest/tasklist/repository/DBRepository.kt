package com.milnest.tasklist.repository

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
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

object DBRepository {
    private var db: SQLiteDatabase = TaskDatabaseHelper(app.context).writableDatabase

    fun getAllTasks(): MutableList<TaskListItem> {
        val cursor = db.query(TaskDatabaseHelper.TABLE, null,
                null, null, null, null, null)
        val list = cursorToList(cursor)
        cursor.close()
        return list
    }

    private fun cursorToList(cursor: Cursor): MutableList<TaskListItem> {
        val taskList: MutableList<TaskListItem> = ArrayList()
        val indexId = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_ID)
        val indexName = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_NAME)
        val indexContent = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_CONTENT)
        val indexType = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_TYPE)
        var id: Int
        var name: String
        var type: Int
        var content: String

        while (cursor.moveToNext()) {
            id = cursor.getInt(indexId)
            name = cursor.getString(indexName)
            type = cursor.getInt(indexType)
            content = cursor.getString(indexContent)

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
        return taskList
    }

    fun addTask(name: String, type: Int, content: String) {
        try {
            val cv = ContentValues()
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name)
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type)
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content)
            db.insert(TaskDatabaseHelper.TABLE, TaskDatabaseHelper.COLUMN_ID, cv)
        } catch (e: SQLException) {
            Log.e("ERROR", e.toString())
        }

    }

    fun getTaskById(id: Int): TaskListItem? {
        val cursor = db.query(TaskDatabaseHelper.TABLE, null,
                TaskDatabaseHelper.COLUMN_ID + "=$id", null, null, null, null)

        val indexName = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_NAME)
        val indexType = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_TYPE)
        val indexContent = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_CONTENT)

        var name = ""
        var content = ""
        var type = -1
        if (cursor.moveToNext()) {
            name = cursor.getString(indexName)
            content = cursor.getString(indexContent)
            type = cursor.getInt(indexType)
        }
        cursor.close()

        when (type) {
            TaskListItem.TYPE_ITEM_TEXT -> return TextTaskListItem(id, name, content)
            TaskListItem.TYPE_ITEM_IMAGE -> {
                return ImgTaskListItem(id, name, PhotoInteractor.createImage(content))
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                return JsonAdapter.fromJson(content)
            }
        }
        return null
        //return arrayOf(name, content)
    }

    fun updateTask(id: Int, name: String, type: Int, content: String) {
        val cv = ContentValues()
        cv.put(TaskDatabaseHelper.COLUMN_NAME, name)
        cv.put(TaskDatabaseHelper.COLUMN_TYPE, type)
        cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content)

        db.update(TaskDatabaseHelper.TABLE, cv,
                TaskDatabaseHelper.COLUMN_ID + " =?",
                arrayOf(id.toString())).toLong()
    }

    fun deleteTask(id: Long) {
        db.delete(TaskDatabaseHelper.TABLE, TaskDatabaseHelper.COLUMN_ID +
                " =?", arrayOf(id.toString()))
    }

    fun searchDynamicTask(data: String): MutableList<TaskListItem> {
        val cursor = db.rawQuery("SELECT * FROM task_table " +
                "WHERE name OR content LIKE '%$data%'", null)
        return cursorToList(cursor)
    }
}

