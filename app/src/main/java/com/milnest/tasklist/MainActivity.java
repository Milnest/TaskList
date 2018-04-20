package com.milnest.tasklist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<TaskListItem> mTaskListItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private GridLayoutManager mGridManager = new GridLayoutManager(this, 2);
    private LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
    public android.support.v7.view.ActionMode mActionMode;
    public android.support.v7.view.ActionMode.Callback mActionModeCallback;
    private ItemsAdapter adapter;
    private final int REQUEST_ACCESS_TYPE = 1;
    public static final String NAME = "NAME";
    public static final String TEXT = "TEXT";
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
        adapter = new ItemsAdapter(mTaskListItems, this);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
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

        initRecyclerView();
        adapter.initActionMode();
        //Layout Manager init
        recyclerView.setLayoutManager(mLinearLayoutManager);
    }

    public void OnClick(View view){
        switch (view.getId()){
            case R.id.add_task_text:
                Intent textIntent = new Intent(this, TextTaskActivity.class);
                startActivityForResult(textIntent, REQUEST_ACCESS_TYPE);
                break;
            case R.id.add_task_photo:
                break;
            case R.id.add_task_list:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_ACCESS_TYPE){
            if(resultCode==RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){
                    String name = data.getStringExtra(NAME);
                    String text = data.getStringExtra(TEXT);
                    adapter.notifyDataSetChanged();
                    mTaskListItems.add(new TextTaskListItem(name, text));
                }
            }
            else{
                Toast.makeText(this, R.string.save_canceled, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
