package com.milnest.tasklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListTaskActivity extends AppCompatActivity {

    private TextView newCheckbox;
    private Toolbar mToolbar;
    //Пара значений, чекбокс и его редактируемый текст
    private List<Pair> mCheckBoxList;
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
    }

    private void setInitialData() {
        newCheckbox = (TextView) findViewById(R.id.new_cb);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        addListTaskLayout = (LinearLayout)findViewById(R.id.add_list_task_layout);
        mCheckBoxList = new ArrayList<>();
        extras = getIntent().getExtras();
        if (extras != null) {
            String[] data = extras.getStringArray("data");
            mListData = data[1];
            ListOfCheckboxesTaskListItem cbList = JsonAdapter.fromJson(mListData);
            mId = extras.getInt("id");
            for (CheckboxTaskListItem item: cbList.getCbList()
                    ) {
                /*CheckBox cb = new CheckBox(this);
                cb.setText(item.getCbText());
                cb.setChecked(item.isCbState());
                addCb(cb);*/
                CheckBox cb = new CheckBox(this);
                //cb.setText(item.getCbText());
                cb.setChecked(item.isCbState());
                EditText cbText = new EditText(this);
                cbText.setText(item.getCbText());
                addCb(cb, cbText);
            }
        }
        else{
            /*CheckBox startCb = new CheckBox(this);
            addCb(startCb);*/
            CheckBox startCb = new CheckBox(this);
            EditText addedCbText = new EditText(this);
            addCb(startCb, addedCbText);
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
                /*CheckBox addedCb = new CheckBox(this);
                addCb(addedCb);*/
                CheckBox addedCb = new CheckBox(this);
                EditText addedCbText = new EditText(this);
                addCb(addedCb, addedCbText);
        }
    }

    private void addCb(final CheckBox cbToAdd, EditText cbTextToAdd) {
        //cbToAdd.setHint("add text here");
        ChangeCbColor.change(cbToAdd);
        cbToAdd.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.
                WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT/*, 0.2f*/));
        final LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.addView(cbToAdd);
        cbTextToAdd.setHint(R.string.new_text);
        cbTextToAdd.setLayoutParams(new LinearLayout.LayoutParams( 0/*LinearLayout.LayoutParams.
                WRAP_CONTENT*/, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
        innerLayout.addView(cbTextToAdd);
        TextView delTextView = new TextView(this);
        delTextView.setText("X");
        delTextView.setTextColor(getResources().getColor(R.color.lum_red));
        delTextView.setLayoutParams(new LinearLayout.LayoutParams( 0/*LinearLayout.LayoutParams.
                WRAP_CONTENT*/, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
        //mCheckBoxList.add(cbToAdd);
        final Pair cbAndText = new Pair<CheckBox, EditText>(cbToAdd, cbTextToAdd);
        mCheckBoxList.add(cbAndText);
        addListTaskLayout.addView(innerLayout);
        innerLayout.addView(delTextView);
        delTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListTaskLayout.removeView(innerLayout);
                mCheckBoxList.remove(cbAndText);
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
        for (Pair cb: mCheckBoxList
             ) {
            if(taskCbList.getCbList()!=null){
                itemList.add((new CheckboxTaskListItem( ((EditText)cb.second).getText().toString(),
                        ((CheckBox)cb.first).isChecked())));

            }
        }
        taskCbList.setCbList(itemList);

        //id просто игнорируется при добавлении нового активити
        data.putExtra(MainActivity.LIST, JsonAdapter.toJson(taskCbList));
        setResult(RESULT_OK, data);
    }

}
