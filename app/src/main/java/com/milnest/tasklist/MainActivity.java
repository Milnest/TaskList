package com.milnest.tasklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<TaskListItem> mTaskListItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitialData();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // создаем адаптер
        ItemsAdapter adapter = new ItemsAdapter(mTaskListItems, this);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
    }

    private void setInitialData() {
        mTaskListItems.add(new TextTaskListItem("name1", "description1"));
        mTaskListItems.add(new TextTaskListItem("name2", "description2"));
        mTaskListItems.add(new TextTaskListItem("name3", "description3"));
        mTaskListItems.add(new TextTaskListItem("name4", "description4"));
        mTaskListItems.add(new ImgTaskListItem("img1", android.R.mipmap.sym_def_app_icon));
    }
}
