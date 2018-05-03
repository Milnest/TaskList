package com.milnest.tasklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by t-yar on 21.04.2018.
 */

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task_data.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "task_table"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CONTENT = "content";
    public static final int TYPE_ITEM_TEXT = 0;
    public static final int TYPE_ITEM_IMAGE = 1;

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ COLUMN_NAME
                + " TEXT, " + COLUMN_TYPE + " INTEGER, "+ COLUMN_CONTENT
                + " TEXT);");

        /*db.execSQL("CREATE VIRTUAL TABLE fts_task_table USING fts4 (content=" +
                "'task_table', name, type, content)");*/

        db.execSQL("CREATE VIRTUAL TABLE fts_task_table USING fts4 (_id, name, type, content)");

        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_ID + ", " + COLUMN_NAME
                + ", " + COLUMN_TYPE  + ", " + COLUMN_CONTENT + ") VALUES (0,'Task1', " +
                TYPE_ITEM_TEXT + ", 'task_content');");

        db.execSQL("INSERT INTO fts_task_table (_id, name, type, content) SELECT _id, " +
                "name, type, content FROM task_table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
    }
}
