package com.milnest.tasklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by t-yar on 21.04.2018.
 */

public class DBAdapter {
    Context c;
    SQLiteDatabase db;
    TaskDatabaseHelper helper;

    public DBAdapter(Context ctx)
    {
        this.c=ctx;
        helper=new TaskDatabaseHelper(c);
    }

    //OPEN DB
    public DBAdapter openDB()
    {
        try
        {
            db=helper.getWritableDatabase();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return this;
    }

    //CLOSE
    public void close()
    {
        try
        {
            helper.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    //INSERT DATA TO DB
    public long add(String name, int type, String content)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name);
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type);
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content);

            return db.insert(TaskDatabaseHelper.TABLE,TaskDatabaseHelper.COLUMN_ID,cv);

        }catch (SQLException e)
        {
            Toast.makeText(c, "Ошибка добавления!", Toast.LENGTH_SHORT).show();
        }

        return 0;
    }

    //RETRIEVE ALL PLAYERS
    public Cursor getAllTasks()
    {
        String[] columns={TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME,
                TaskDatabaseHelper.COLUMN_TYPE, TaskDatabaseHelper.COLUMN_CONTENT};

        return db.query(TaskDatabaseHelper.TABLE,columns,null,null,
                null, null,null);
    }

    //UPDATE
    public long UPDATE(int id, String name, int type, String content)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(TaskDatabaseHelper.COLUMN_NAME, name);
            cv.put(TaskDatabaseHelper.COLUMN_TYPE, type);
            cv.put(TaskDatabaseHelper.COLUMN_CONTENT, content);

            return db.update(TaskDatabaseHelper.TABLE, cv,
                    TaskDatabaseHelper.COLUMN_ID + " =?",
                    new String[]{String.valueOf(id)});

        }catch (SQLException e)
        {
            Toast.makeText(c, "Ошибка добавления!", Toast.LENGTH_SHORT).show();
        }

        return 0;
    }

    //DELETE
    public long Delete(int id)
    {
        try
        {
            return db.delete(TaskDatabaseHelper.TABLE,TaskDatabaseHelper.COLUMN_ID +
                    " =?",new String[]{String.valueOf(id)});

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

}
