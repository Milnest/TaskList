package com.milnest.tasklist;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

/**
 * Created by t-yar on 22.04.2018.
 */

/**Класс для обобщения работы с базой данных
 * Пока не используется
 * TODO перенести функциональность DBAdapter-а в Helper, а функции для БД из main activity - сюда.
 * */
public class DBMethodsAdapter {
    public void save(String name, int type, String content, Context context)
    {
        DBAdapter db=new DBAdapter(context);

        //OPEN
        db.openDB();

        //INSERT
        long result=db.add(name, type, content);

        if(result>0)
        {
            Toast.makeText(context,"Задача добавлена!", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(context,"Ошибка добавления!", Toast.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //refresh
        retrieve(context);

    }

    public void retrieve(Context context)
    {
        DBAdapter db=new DBAdapter(context);

        //OPEN
        db.openDB();

        MainActivity.mTaskListItems.clear();

        //SELECT
        Cursor c=db.getAllTasks();

        //LOOP THRU THE DATA ADDING TO ARRAYLIST
        while (c.moveToNext())
        {
            int id = c.getInt(0);
            String name = c.getString(1);
            int type = c.getInt(2);
            String content = c.getString(3);

            //CREATE PLAYER
            TaskListItem taskListItem = new TextTaskListItem(id, name, content);
            //TODO картинка
            //Player p=new Player(name,pos,id);

            //ADD TO PLAYERS
            MainActivity activity = (MainActivity) context;
            activity.adapter.notifyDataSetChanged();
            MainActivity.mTaskListItems.add(taskListItem);
        }

        //SET ADAPTER TO RV
        /*if(!(mTaskListItems.size()<1))
        {
            rv.setAdapter(adapter);
        }*/

    }

    public String[] getById(Context context, int id)
    {
        DBAdapter db=new DBAdapter(context);

        //OPEN
        db.openDB();

        //SELECT
        Cursor c=db.getTaskById(id);

        String name = c.getString(1);
        //int type = c.getInt(2);
        String content = c.getString(3);

        return new String[]{name, content};
        //TODO картинка

    }

    public void edit(int id, String name, int type, String content, Context context)
    {
        DBAdapter db=new DBAdapter(context);

        //OPEN
        db.openDB();

        //INSERT
        long result = db.UPDATE(id, name, type, content);

        if(result>0)
        {
            Toast.makeText(context,"Задача изменена!", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(context,"Ошибка изменения!", Toast.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //refresh
        retrieve(context);

    }
}
