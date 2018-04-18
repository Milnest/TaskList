package com.milnest.tasklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<TaskListItem> mTaskListItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private GridLayoutManager mGridManager = new GridLayoutManager(this, 2);
    private LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
    public android.support.v7.view.ActionMode mActionMode;
    public android.support.v7.view.ActionMode.Callback mActionModeCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitialData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_split:
                if (recyclerView.getLayoutManager() == mLinearLayoutManager)
                recyclerView.setLayoutManager(mGridManager);
                else {
                    recyclerView.setLayoutManager(mLinearLayoutManager);
                }
                break;
        }
        return true;
    }

    /**Инициализирует Recycler*/
    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // создаем адаптер
        ItemsAdapter adapter = new ItemsAdapter(mTaskListItems, this);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        /*recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionMode == null) {
                    mActionMode = startActionMode(mActionModeCallback);
                    mActionMode.setTitle("Action Mode");
                }
                else {
                    mActionMode.finish();
                }
                return true;
            }
        });*/
    }


    /**Заполняет Recycler тестовыми данными и инициализирует View*/
    private void setInitialData() {
        //Recycler data
        mTaskListItems.add(new TextTaskListItem("name1", "description1"));
        mTaskListItems.add(new TextTaskListItem("name2", "description2"));
        mTaskListItems.add(new TextTaskListItem("name3", "description3"));
        //mTaskListItems.add(new TextTaskListItem("name4", "description4"));
        mTaskListItems.add(new ImgTaskListItem("img1", android.R.mipmap.sym_def_app_icon));
        //View init
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initActionMode();
        initRecyclerView();
        //Layout Manager init
        recyclerView.setLayoutManager(mLinearLayoutManager);
    }

    private void initActionMode() {
        mActionModeCallback = new android.support.v7.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context_task, menu);
               /* menuInflater.inflate(R.menu.menu_context_task, menu);*/
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
                mActionMode = null;
            }
            /*@Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                *//*MenuInflater inflater = mode.getMenuInflater();*//*
                *//*inflater.inflate(R.menu.menu_context_task, menu);*//*
                menuInflater.inflate(R.menu.menu_context_task, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                *//*recyclerView.removeView();*//*
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }*/
        };
    }
}
