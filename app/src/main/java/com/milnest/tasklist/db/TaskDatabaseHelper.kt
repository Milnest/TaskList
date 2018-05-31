package com.milnest.tasklist.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by t-yar on 21.04.2018.
 */

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
                + " TEXT, " + COLUMN_TYPE + " INTEGER, " + COLUMN_CONTENT
                + " TEXT);")

        /*db.execSQL("CREATE VIRTUAL TABLE fts_task_table USING fts4 (content=" +
                "'task_table', name, type, content)");*/

        /*db.execSQL("CREATE VIRTUAL TABLE fts_task_table USING fts4 (_id, name, type, content)")*/
        db.execSQL("CREATE INDEX ix_name_content" +
                " ON task_table(name, content);")

        db.execSQL("INSERT INTO " + TABLE + " (" + COLUMN_ID + ", " + COLUMN_NAME
                + ", " + COLUMN_TYPE + ", " + COLUMN_CONTENT + ") VALUES (0,'Task1', " +
                TYPE_ITEM_TEXT + ", 'task_content');")

        //db.execSQL("INSERT INTO fts_task_table (_id, name, type, content) SELECT _id, " + "name, type, content FROM task_table")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${TABLE}")
    }

    companion object {
        private val DATABASE_NAME = "task_data.db"
        private val SCHEMA = 1
        internal val TABLE = "task_table"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "name"
        val COLUMN_TYPE = "type"
        val COLUMN_CONTENT = "content"
        val TYPE_ITEM_TEXT = 0
    }
}
