package com.milnest.tasklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListTaskActivity extends AppCompatActivity {

    private TextView newCheckbox;
    private CheckBox startCb;
    private Toolbar mToolbar;
    List<CheckBox> mCheckBoxList;
    LinearLayout addListTaskLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        setInitialData();
    }

    private void setInitialData() {
        newCheckbox = (TextView) findViewById(R.id.new_cb);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        startCb = (CheckBox) findViewById(R.id.cb_start);
        addListTaskLayout = (LinearLayout)findViewById(R.id.add_list_task_layout);
        mCheckBoxList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        /*Log.d(TAG, "onCreateOptionsMenu: Вызов инфлейтера");*/
        /*taskTitle.setText("called");*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()){
            case R.id.action_task_text_save:
                saveList();
                finish();
                break;
        }*/
        saveList();
        finish();
        return true;
    }

    public void OnClick(View view) {
        switch (view.getId()){
            case R.id.new_cb:
                CheckBox addedCb = new CheckBox(this);
                addedCb.setHint("add text here");
                addListTaskLayout.addView(addedCb);
                mCheckBoxList.add(addedCb);
        }
    }

    private void saveList() {
        Intent data = new Intent();
        ListOfCheckboxesTaskListItem taskCbList = new ListOfCheckboxesTaskListItem(
                0, "", TaskListItem.TYPE_ITEM_LIST,
                new ArrayList<CheckboxTaskListItem>());
        List<CheckboxTaskListItem> itemList = new ArrayList<>();
        for (CheckBox cb: mCheckBoxList
             ) {
            if(taskCbList.getCbList()!=null){
                /*taskCbList.getCbList().add(new CheckboxTaskListItem(cb.getText().toString(),
                        cb.isChecked()));*/
                itemList.add((new CheckboxTaskListItem(cb.getText().toString(),
                        cb.isChecked())));

            }
            //тут мб косяк
        }
        taskCbList.setCbList(itemList);

        //id просто игнорируется при добавлении нового активити
        data.putExtra(MainActivity.LIST, JsonAdapter.toJson(taskCbList));
        /*if (mId != null){
            data.putExtra(MainActivity.ID, mId);
        }*/
        setResult(RESULT_OK, data);
    }
}
