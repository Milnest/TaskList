package com.milnest.tasklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListTaskActivity extends AppCompatActivity {

    private TextView newCheckbox;
    private Toolbar mToolbar;
    private List<CheckBox> mCheckBoxList;
    private LinearLayout addListTaskLayout;
    private Integer mId;
    private String mListData;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        setInitialData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        extras = getIntent().getExtras();
        if (extras != null) {
            String[] data = extras.getStringArray("data");
            mListData = data[1];
            ListOfCheckboxesTaskListItem cbList = JsonAdapter.fromJson(mListData);
            mId = extras.getInt("id");
            for (CheckboxTaskListItem item: cbList.getCbList()
                    ) {
                CheckBox cb = new CheckBox(this);
                cb.setText(item.getCbText());
                cb.setChecked(item.isCbState());
                addCb(cb);
            }
        }
    }

    private void setInitialData() {
        newCheckbox = (TextView) findViewById(R.id.new_cb);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        addListTaskLayout = (LinearLayout)findViewById(R.id.add_list_task_layout);
        mCheckBoxList = new ArrayList<>();
        if (extras == null){
            CheckBox startCb = new CheckBox(this);
            addCb(startCb);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveList();
        finish();
        return true;
    }

    public void OnClick(View view) {
        switch (view.getId()){
            case R.id.new_cb:
                CheckBox addedCb = new CheckBox(this);
                addCb(addedCb);
        }
    }

    private void addCb(final CheckBox cbToAdd) {
        cbToAdd.setHint("add text here");
        cbToAdd.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.
                WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.75f));
        final LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.addView(cbToAdd);
        TextView delTextView = new TextView(this);
        delTextView.setText("X");
        delTextView.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.
                WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
        mCheckBoxList.add(cbToAdd);
        addListTaskLayout.addView(innerLayout);
        innerLayout.addView(delTextView);
        delTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListTaskLayout.removeView(innerLayout);
                mCheckBoxList.remove(cbToAdd);
            }
        });
    }



    private void saveList() {
        Intent data = new Intent();
        ListOfCheckboxesTaskListItem taskCbList;
        if (mId != null){
            taskCbList = new ListOfCheckboxesTaskListItem(
                    mId, "", TaskListItem.TYPE_ITEM_LIST,
                    new ArrayList<CheckboxTaskListItem>());
            data.putExtra(MainActivity.ID, mId);
        }
        else{
        taskCbList = new ListOfCheckboxesTaskListItem(
                0, "", TaskListItem.TYPE_ITEM_LIST,
                new ArrayList<CheckboxTaskListItem>());
        }
        List<CheckboxTaskListItem> itemList = new ArrayList<>();
        for (CheckBox cb: mCheckBoxList
             ) {
            if(taskCbList.getCbList()!=null){
                itemList.add((new CheckboxTaskListItem(cb.getText().toString(),
                        cb.isChecked())));

            }
        }
        taskCbList.setCbList(itemList);

        //id просто игнорируется при добавлении нового активити
        data.putExtra(MainActivity.LIST, JsonAdapter.toJson(taskCbList));
        setResult(RESULT_OK, data);
    }

}
