package com.milnest.tasklist.repository

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.milnest.tasklist.TaskDatabaseHelper

/**
 * Created by t-yar on 21.04.2018.
 */

class DBAdapter private constructor(internal var c: Context) {
    internal lateinit var db: SQLiteDatabase
    internal var helper: TaskDatabaseHelper

    //RETRIEVE ALL TASKS
    val allTasks: Cursor
        get() {
            val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME, TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)

            return db.query(TaskDatabaseHelper.TABLE, columns, null, null, null, null, null)
        }

    init {
        helper = TaskDatabaseHelper(c)
    }

    //OPEN DB
    fun openDB(): DBAdapter {
        try {
            db = helper.writableDatabase
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return this
    }

    //CLOSE
    fun close() {
        try {
            helper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    //INSERT DATA TO DB
    fun add(name: String, type: Int, content: String): Long {
        try {
            val cv = ContentValues()
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name)
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type)
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content)

            db.execSQL("INSERT INTO fts_task_table (_id, name, type, content) SELECT _id, " + "name, type, content FROM task_table")

            return db.insert(TaskDatabaseHelper.TABLE, TaskDatabaseHelper.COLUMN_ID, cv)

        } catch (e: SQLException) {
            Toast.makeText(c, "Ошибка добавления!", Toast.LENGTH_SHORT).show()
        }

        return 0
    }

    fun getTaskById(id: Int): Cursor {
        val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME, TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)
        /*return db.rawQuery("select * from " + TaskDatabaseHelper.TABLE + " WHERE " +
                    TaskDatabaseHelper.COLUMN_ID + "= " + id, null);*/
        return db.query(TaskDatabaseHelper.TABLE, columns, TaskDatabaseHelper.COLUMN_ID +
                "=" + id, null, null, null, null)
    }

    //UPDATE
    fun UPDATE(id: Int, name: String, type: Int, content: String): Long {
        try {
            val cv = ContentValues()
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name)
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type)
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content)

            db.execSQL("INSERT INTO fts_task_table (_id, name, type, content) SELECT _id, " + "name, type, content FROM task_table")

            return db.update(TaskDatabaseHelper.TABLE, cv,
                    TaskDatabaseHelper.COLUMN_ID + " =?",
                    arrayOf(id.toString())).toLong()

        } catch (e: SQLException) {
            Toast.makeText(c, "Ошибка добавления!", Toast.LENGTH_SHORT).show()
        }

        return 0
    }

    //DELETE
    fun Delete(id: Int): Long {
        val del: Int
        try {
            del = db.delete(TaskDatabaseHelper.TABLE, TaskDatabaseHelper.COLUMN_ID + " =?", arrayOf(id.toString()))
            db.execSQL("INSERT INTO fts_task_table (_id, name, type, content) SELECT _id, " + "name, type, content FROM task_table")
            return del.toLong()

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return 0
    }

    fun Search(data: String): Cursor {
        val columns = arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME, TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT)

        return db.rawQuery("select * from task_table where name = ? OR content = ?", arrayOf(data, data))
    }

    fun SearchDynamic(data: String): Cursor {
        /*String[] columns={TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT};*/
        val selectionArgs = arrayOf(data)
        //return db.rawQuery("select * from task_table where name LIKE ? OR content = LIKE ?", new String[]{data, data});
        val cursor = db.rawQuery("SELECT * FROM fts_task_table " + "WHERE fts_task_table MATCH ?", selectionArgs)
        return cursor
    }

    companion object {
        private var dbAdapter: DBAdapter? = null
        fun setDBAdapter(con: Context){
            if(dbAdapter == null) {
                dbAdapter = DBAdapter(con)
            }
        }
        fun getDBAdapter() = dbAdapter
    }

}

